package com.azer.csvmergesort.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Service
public class CsvProcessingService {

    private final List<Character> possibleSeparators = Arrays.asList(',', ';', '\t');

    public char detectSeparator(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String firstLine = br.readLine();
            if (firstLine == null) {
                throw new IOException("Empty file");
            }

            int maxCount = -1;
            char detectedSeparator = ',';

            for (char sep : possibleSeparators) {
                int count = countOccurrences(firstLine, sep);
                if (count > maxCount) {
                    maxCount = count;
                    detectedSeparator = sep;
                }
            }
            return detectedSeparator;
        }
    }

    private int countOccurrences(String line, char sep) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == sep) count++;
        }
        return count;
    }

    public List<String> extractHeaders(File file) throws IOException {
        char separator = detectSeparator(file);

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();

        try (CSVReader reader = new CSVReaderBuilder(new FileReader(file))
                .withCSVParser(parser)
                .build()) {

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
