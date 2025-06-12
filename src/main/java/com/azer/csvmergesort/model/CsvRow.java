package com.azer.csvmergesort.model;
import java.util.Map;

public class CsvRow implements Comparable<CsvRow> {
    private Map<String, String> fields;
    private String sortType;
    private String sortColumn;

    public CsvRow(Map<String, String> fields , String sortType , String sortColumn) {
        this.fields = fields;
        this.sortType = sortType;
        this.sortColumn = sortColumn;
    }

    public Map<String, String> getFields() { return fields; }
    public String getSortType() { return sortType; }
    public void setSortKey(String sortKey) { this.sortType = sortKey; }
    public String getSsortColumn() { return sortColumn; }
    public void setSortColumny(String sortColumn) { this.sortColumn = sortColumn; }

    @Override
    public int compareTo(CsvRow other) {
        if(this.sortType.equals("string")){
        return this.fields.get(sortColumn).compareToIgnoreCase(other.fields.get(sortColumn));
        }else{
            if(Double.parseDouble(this.fields.get(sortColumn)) > Double.parseDouble(other.fields.get(sortColumn))){
                return 1;
            } else if (Double.parseDouble(this.fields.get(sortColumn)) < Double.parseDouble(other.fields.get(sortColumn))) {
                return -1 ;
            }else{
                return 0;
            }
        }
    }
}
