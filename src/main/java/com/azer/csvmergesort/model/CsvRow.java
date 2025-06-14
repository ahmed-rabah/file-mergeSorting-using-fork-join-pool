//package com.azer.csvmergesort.model;
//import java.util.Map;
//
//public class CsvRow implements Comparable<CsvRow> {
//    private Map<String, String> fields;
//    private String sortType;
//    private String sortColumn;
//
//    public CsvRow(Map<String, String> fields, String sortType, String sortColumn) {
//        this.fields = fields;
//        this.sortType = sortType;
//        this.sortColumn = sortColumn;
//    }
//
//    public Map<String, String> getFields() { return fields; }
//    public String getSortType() { return sortType; }
//    public void setSortType(String sortType) { this.sortType = sortType; }
//    public String getSortColumn() { return sortColumn; }
//    public void setSortColumn(String sortColumn) { this.sortColumn = sortColumn; }
//
//    @Override
//    public int compareTo(CsvRow other) {
//        String val1 = fields.get(sortColumn);
//        String val2 = other.fields.get(sortColumn);
//
//        if ("string".equalsIgnoreCase(sortType)) {
//            return val1.compareToIgnoreCase(val2);
//        } else {
//            try {
//                double num1 = Double.parseDouble(val1);
//                double num2 = Double.parseDouble(val2);
//                return Double.compare(num1, num2);
//            } catch (NumberFormatException e) {
//                return 0; // ou lève une exception personnalisée
//            }
//        }
//    }
//}
package com.azer.csvmergesort.model;

import java.util.Map;

public class CsvRow implements Comparable<CsvRow> {
    private Map<String, String> fields;
    private String sortType;
    private String sortColumn;

    public CsvRow(Map<String, String> fields, String sortType, String sortColumn) {
        this.fields = fields;
        this.sortType = sortType;
        this.sortColumn = sortColumn;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    @Override
    public int compareTo(CsvRow other) {
        String val1 = cleanValue(fields.get(sortColumn));
        String val2 = cleanValue(other.fields.get(sortColumn));

        if ("string".equalsIgnoreCase(sortType)) {
            return val1.compareToIgnoreCase(val2);
        } else {
            try {
                double num1 = Double.parseDouble(val1);
                double num2 = Double.parseDouble(val2);
                return Double.compare(num1, num2);
            } catch (NumberFormatException e) {
                return 0; // Si la conversion échoue, les lignes sont considérées comme égales
            }
        }
    }

    /**
     * Nettoie une valeur : supprime les guillemets, les espaces et vérifie le null.
     */
    private String cleanValue(String value) {
        if (value == null) return "";
        return value.replaceAll("\"", "").trim();
    }
}
