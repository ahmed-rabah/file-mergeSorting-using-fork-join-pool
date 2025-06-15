package com.azer.csvmergesort.service;

import com.azer.csvmergesort.model.CsvRow;
import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class CsvService {

    private final List<Character> possibleSeparators = Arrays.asList(',', ';', '\t', '|');

    /**
     * Reads a CSV file and maps each row into CsvRow objects.
     * Automatically detects the separator.
     */
    public List<CsvRow> readCsv(InputStream inputStream, String sortType, String sortColumn)
            throws IOException, CsvValidationException {

        // Wrap the InputStream to allow reset
        BufferedInputStream bufferedInput = new BufferedInputStream(inputStream);
        bufferedInput.mark(2048); // mark the beginning for reset

        char separator = detectSeparator(bufferedInput);

        // Reset stream after detection
        bufferedInput.reset();

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .build();

        List<CsvRow> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(bufferedInput))
                .withCSVParser(parser)
                .build()) {

            String[] header = reader.readNext();
            if (header == null) throw new IOException("CSV file has no header");

            String[] line;
            while ((line = reader.readNext()) != null) {
                Map<String, String> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < header.length; i++) {
                    rowMap.put(header[i], i < line.length ? line[i] : "");
                }
                rows.add(new CsvRow(rowMap, sortType, sortColumn));
            }
        }

        return rows;
    }

    /**
     * Writes rows back to CSV using the provided separator.
     */
    public File writeCsv(List<CsvRow> rows, char separator) throws IOException {
        File file = File.createTempFile("sorted", ".csv");

        CSVWriter writer = new CSVWriter(
                new FileWriter(file),
                separator,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
        );

        try (writer) {
            if (rows.isEmpty()) return file;

            List<String> headers = new ArrayList<>(rows.get(0).getFields().keySet());
            writer.writeNext(headers.toArray(new String[0]));

            for (CsvRow row : rows) {
                String[] values = headers.stream()
                        .map(h -> row.getFields().getOrDefault(h, ""))
                        .toArray(String[]::new);
                writer.writeNext(values);
            }
        }

        return file;
    }

    /**
     * Detects the most probable separator based on the first line of the CSV.
     */
    public char detectSeparator(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = reader.readLine();

        if (firstLine == null) throw new IOException("Empty CSV file");

        int maxCount = -1;
        char detected = ',';

        for (char sep : possibleSeparators) {
            int count = countOccurrences(firstLine, sep);
            if (count > maxCount) {
                maxCount = count;
                detected = sep;
            }
        }

        return detected;
    }

    private int countOccurrences(String line, char sep) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == sep) count++;
        }
        return count;
    }
}
