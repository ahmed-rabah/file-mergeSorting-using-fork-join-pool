package com.azer.csvmergesort.controller;

import com.azer.csvmergesort.model.CsvRow;
import com.azer.csvmergesort.model.SortRequest;
import com.azer.csvmergesort.service.CsvService;
import com.azer.csvmergesort.service.CsvProcessingService ;
import com.azer.csvmergesort.service.MergeSortTask;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("/api")
public class CsvSortController {
    private final CsvService csvService;
    private final CsvProcessingService csvProcessingService;
    private final ForkJoinPool pool = new ForkJoinPool();

    public CsvSortController(CsvService csvService  , CsvProcessingService csvProcessingService) {
        this.csvService = csvService;
        this.csvProcessingService =  csvProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<String>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(csvProcessingService.extractHeaders(file));
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
   @PostMapping("/sort")
    public ResponseEntity<FileSystemResource> sortCsv(@RequestBody SortRequest request) throws Exception {
        File file = new File(request.getFilePath());
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.badRequest().build();
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            List<CsvRow> rows = csvService.readCsv(inputStream,request.getSortColumnType(),request.getSortColumn());
            long start = System.currentTimeMillis();
            List<CsvRow> sorted = pool.invoke(new MergeSortTask(rows));
            long end = System.currentTimeMillis();
            System.out.println("Temps de tri : " + (end - start) + " ms");
            File result = csvService.writeCsv(sorted);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=sorted.csv")
                    .body(new FileSystemResource(result));
        }
    }

    @PostMapping("/sort-seq")
    public ResponseEntity<FileSystemResource> sortSequentially(@RequestBody SortRequest request)
            throws Exception {
        File file = new File(request.getFilePath());
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.badRequest().build();
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            List<CsvRow> rows = csvService.readCsv(inputStream, request.getSortColumnType(),
                    request.getSortColumn());

            long start = System.currentTimeMillis();
            Collections.sort(rows);  // tri séquentiel
            long end = System.currentTimeMillis();
            System.out.println("Tri séquentiel : " + (end - start) + " ms");

            File result = csvService.writeCsv(rows);
            return ResponseEntity.ok()
                    .header("Content-Disposition"
                            , "attachment; filename=sorted_sequential.csv")
                    .body(new FileSystemResource(result));
        }
    }

}
