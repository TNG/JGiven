package com.tngtech.jgiven.report.model;

import com.tngtech.jgiven.annotation.Table.HeaderType;

import java.util.List;

/**
 * Represents a data table argument.
 */
public class DataTable {

    /**
     * The type of the header
     */
    private HeaderType headerType;

    /**
     * The data of the table.
     */
    private List<List<String>> data;

    public DataTable(HeaderType headerType, List<List<String>> data) {
        this.headerType = headerType;
        this.data = data;
    }

    public HeaderType getHeaderType() {
        return headerType;
    }

    public void setHeaderType(HeaderType headerType) {
        this.headerType = headerType;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
}
