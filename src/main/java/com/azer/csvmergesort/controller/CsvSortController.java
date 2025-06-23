package com.azer.csvmergesort.controller;

import com.azer.csvmergesort.model.CsvRow;
import com.azer.csvmergesort.model.SortRequest;
import com.azer.csvmergesort.service.CsvProcessingService;
import com.azer.csvmergesort.service.CsvService;
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

    // 1. Formulaire d’upload
    @GetMapping("/upload")
    public String showUploadForm() {
        return "upload";
    }

    // 2. Traiter l’upload et extraire les colonnes
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) throws Exception {
        uploadedFile = File.createTempFile("upload-", ".csv");
        file.transferTo(uploadedFile);

        List<String> headers = csvProcessingService.extractHeaders(uploadedFile);
        model.addAttribute("headers", headers);
        model.addAttribute("filePath", uploadedFile.getAbsolutePath());

        return "choose-column";
    }

    // 3. Traiter le tri
    @PostMapping("/sort")
    public String sortCsv(@RequestParam String filePath,
                          @RequestParam String sortType,
                          @RequestParam String column,
                          @RequestParam String method,
                          Model model) throws Exception {

        try (BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(filePath))) {
            bufferedInput.mark(2048);

            char separator = csvService.detectSeparator(bufferedInput);
            bufferedInput.reset();

            List<CsvRow> rows = csvService.readCsv(bufferedInput, sortType, column);

            if ("parallel".equals(method)) {
                sortedFile = csvService.writeCsv(pool.invoke(new MergeSortTask(rows)), separator);
            } else {
                Collections.sort(rows);
                sortedFile = csvService.writeCsv(rows, separator);
            }

            model.addAttribute("filename", sortedFile.getName());
            return "download";
        }
    }

    // 4. Télécharger le fichier trié
    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<FileSystemResource> downloadSortedFile() {
        if (sortedFile == null || !sortedFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + sortedFile.getName())
                .body(new FileSystemResource(sortedFile));
    }

    // (Optionnel) Endpoint API : tri séquentiel depuis JSON
    @PostMapping("/sort-seq")
    public ResponseEntity<FileSystemResource> sortSequentially(@RequestBody SortRequest request) throws Exception {
        File file = new File(request.getFilePath());
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.badRequest().build();
        }

        try (BufferedInputStream bufferedInput = new BufferedInputStream(new FileInputStream(file))) {
            bufferedInput.mark(2048);

            char separator = csvService.detectSeparator(bufferedInput);
            bufferedInput.reset();

            List<CsvRow> rows = csvService.readCsv(bufferedInput, request.getSortColumnType(), request.getSortColumn());
            long start = System.currentTimeMillis();
            Collections.sort(rows);
            long end = System.currentTimeMillis();



            System.out.println("Tri séquentiel : " + (end - start) + " ms");

            File result = csvService.writeCsv(rows, separator);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=sorted_sequential.csv")
                    .body(new FileSystemResource(result));
        }
    }
}
