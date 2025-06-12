package com.azer.csvmergesort.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvProcessingService {

    private final String uploadDir = "./upload";

    public List<String> extractHeaders(MultipartFile file) throws IOException {
        if (!Files.exists(Path.of(uploadDir))) {
            Files.createDirectories(Path.of(uploadDir));
        }

        Path filePath = Path.of(uploadDir, file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] headers = reader.readNext(); // read the first row as headers
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
