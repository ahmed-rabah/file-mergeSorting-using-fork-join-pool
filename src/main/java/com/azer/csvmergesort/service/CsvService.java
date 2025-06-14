package com.azer.csvmergesort.service;
import com.azer.csvmergesort.model.CsvRow;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class CsvService {

//    public List<CsvRow> readCsv(InputStream inputStream , String sortType , String sortColumn)
//            throws IOException, CsvValidationException{
//        List<CsvRow> rows = new ArrayList<>();
//        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
//            String[] header = reader.readNext();
//            String[] line;
//            while ((line = reader.readNext()) != null) {
//                Map<String, String> rowMap = new LinkedHashMap<>();
//                for (int i = 0; i < header.length; i++) {
//                    rowMap.put(header[i], line[i]);
//                }
//                rows.add(new CsvRow(rowMap , sortType , sortColumn));
//            }
//        }
//        return rows;
//    }
public List<CsvRow> readCsv(InputStream inputStream, String sortType, String sortColumn)
        throws IOException, CsvValidationException {
    List<CsvRow> rows = new ArrayList<>();
    try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
        String[] header = reader.readNext();
        if (header == null) {
            throw new IOException("Fichier CSV vide ou invalide");
        }
        String[] line;
        while ((line = reader.readNext()) != null) {
            // ⚠ Ignore les lignes vides ou avec moins de colonnes
            if (line.length < header.length) {
                System.err.println("Ligne ignorée (colonnes manquantes) : " + Arrays.toString(line));
                continue;
            }

            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < header.length; i++) {
                String value = (i < line.length) ? line[i] : ""; // Remplit avec "" si absent
                rowMap.put(header[i], value);
            }
            rows.add(new CsvRow(rowMap, sortType, sortColumn));
        }
    }
    return rows;
}



    public File writeCsv(List<CsvRow> rows) throws IOException {
        File file = File.createTempFile("sorted", ".csv");
        try (CSVWriter writer = new CSVWriter(new FileWriter(file),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER, //  pas de quotes autour des champs
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {
            if (rows.isEmpty()) return file;

            List<String> headers = new ArrayList<>(rows.get(0).getFields().keySet());
            writer.writeNext(headers.toArray(new String[0]));

            for (CsvRow row : rows) {
                String[] values = headers.stream().map(h -> row.getFields().get(h)).toArray(String[]::new);
                writer.writeNext(values);
            }
        }
        return file;
    }
}