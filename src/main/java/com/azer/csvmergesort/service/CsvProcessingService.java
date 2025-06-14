package com.azer.csvmergesort.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvProcessingService {

    private final String uploadDir = "./upload";

    public List<String> extractHeaders(File file) throws IOException {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] headers = reader.readNext();
            if (headers != null) {
                return new ArrayList<>(Arrays.asList(headers));
            } else {
                return new ArrayList<>();
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid CSV file", e);
        }
    }
}
