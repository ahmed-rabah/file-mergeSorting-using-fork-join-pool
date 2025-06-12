package com.azer.csvmergesort.model;

public class SortRequest {
    private String filePath;
    private String sortColumn;
    private String sortColumnType; // "string" or "number"

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortColumnType() {
        return sortColumnType;
    }

    public void setSortColumnType(String sortColumnType) {
        this.sortColumnType = sortColumnType;
    }
}

