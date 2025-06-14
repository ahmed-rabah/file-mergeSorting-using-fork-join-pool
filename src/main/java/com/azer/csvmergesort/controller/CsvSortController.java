package com.azer.csvmergesort.controller;

import com.azer.csvmergesort.model.CsvRow;
import com.azer.csvmergesort.service.CsvService;
import com.azer.csvmergesort.service.CsvProcessingService ;
import com.azer.csvmergesort.service.MergeSortTask;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

    @Controller
    @RequestMapping("/csv")
    public class CsvSortController {
        private final CsvService csvService;
        private final CsvProcessingService csvProcessingService;
        private final ForkJoinPool pool = new ForkJoinPool();

        private File uploadedFile;
        private File sortedFile;

        public CsvSortController(CsvService csvService, CsvProcessingService csvProcessingService) {
            this.csvService = csvService;
            this.csvProcessingService = csvProcessingService;
        }

        @GetMapping("/upload")
        public String showUploadForm() {
            return "upload";  // upload.html (template Thymeleaf)
        }


        @PostMapping("/upload")
        public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws Exception {
            // Sauvegarde temporaire du fichier uploadé
            uploadedFile = File.createTempFile("upload-", ".csv");
            file.transferTo(uploadedFile);

            // Extraire les headers depuis le fichier temporaire (InputStream)
            List<String> headers = csvProcessingService.extractHeaders(uploadedFile);
            model.addAttribute("headers", headers);

            model.addAttribute("filePath", uploadedFile.getAbsolutePath());

            return "choose-column"; // choose-column.html
        }

        @PostMapping("/sort")
        public String sortCsv(@RequestParam String filePath,
                              @RequestParam String sortType,  // récupérer ce paramètre
                              @RequestParam String column,
                              @RequestParam String method,
                              Model model) throws Exception {
            List<CsvRow> rows = csvService.readCsv(new FileInputStream(filePath), sortType, column);
            if ("parallel".equals(method)) {
                sortedFile = csvService.writeCsv(pool.invoke(new MergeSortTask(rows)));
            } else {
                Collections.sort(rows);
                sortedFile = csvService.writeCsv(rows);
            }

            model.addAttribute("filename", sortedFile.getName());
            return "download";  // download.html
        }


        @GetMapping("/download")
        @ResponseBody
        public ResponseEntity<FileSystemResource> downloadSortedFile() throws IOException {
            if (sortedFile == null || !sortedFile.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + sortedFile.getName())
                    .body(new FileSystemResource(sortedFile));
        }
    }
    /*    @PostMapping("/sort")
    public ResponseEntity<FileSystemResource> sortCsv(@RequestParam("file") String filePath,
                                                      @RequestParam("column") String column) throws Exception {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.badRequest().build(); // return 400 if file doesn't exist
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            List<CsvRow> rows = csvService.readCsv(inputStream, column);
            List<CsvRow> sorted = pool.invoke(new MergeSortTask(rows));
            File result = csvService.writeCsv(sorted);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=sorted.csv")
                    .body(new FileSystemResource(result));
        }
  }*/
//   @PostMapping("/sort")
//    public ResponseEntity<FileSystemResource> sortCsv(@RequestBody SortRequest request) throws Exception {
//        File file = new File(request.getFilePath());
//        if (!file.exists() || !file.isFile()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        try (InputStream inputStream = new FileInputStream(file)) {
//            List<CsvRow> rows = csvService.readCsv(inputStream,request.getSortColumnType(),request.getSortColumn());
//            long start = System.currentTimeMillis();
//            List<CsvRow> sorted = pool.invoke(new MergeSortTask(rows));
//            long end = System.currentTimeMillis();
//            System.out.println("Temps de tri : " + (end - start) + " ms");
//            File result = csvService.writeCsv(sorted);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition", "attachment; filename=sorted.csv")
//                    .body(new FileSystemResource(result));
//        }
//    }


//    @PostMapping("/sort-seq")
//    public ResponseEntity<FileSystemResource> sortSequentially(@RequestBody SortRequest request)
//            throws Exception {
//        File file = new File(request.getFilePath());
//        if (!file.exists() || !file.isFile()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        try (InputStream inputStream = new FileInputStream(file)) {
//            List<CsvRow> rows = csvService.readCsv(inputStream, request.getSortColumnType(),
//                    request.getSortColumn());
//
//            long start = System.currentTimeMillis();
//            Collections.sort(rows);  // tri séquentiel
//            long end = System.currentTimeMillis();
//            System.out.println("Tri séquentiel : " + (end - start) + " ms");
//
//            File result = csvService.writeCsv(rows);
//            return ResponseEntity.ok()
//                    .header("Content-Disposition"
//                            , "attachment; filename=sorted_sequential.csv")
//                    .body(new FileSystemResource(result));
//        }
//    }

