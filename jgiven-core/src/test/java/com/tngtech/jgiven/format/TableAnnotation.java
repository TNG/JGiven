package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;

public class TableAnnotation implements Table {

    HeaderType header = HeaderType.HORIZONTAL;
    boolean transpose = false;
    boolean includeNullColumns = false;
    String[] excludeFields = {};
    String[] includeFields = {};
    String[] columnTitles = {};
    boolean numberedRows = false;
    boolean numberedColumns = false;
    String numberedRowsHeader = "";
    String numberedColumnsHeader = "";

    @Override
    public HeaderType header() {
        return header;
    }

    @Override
    public boolean transpose() {
        return transpose;
    }

    @Override
    public String[] excludeFields() {
        return excludeFields;
    }

    @Override
    public String[] includeFields() {
        return includeFields;
    }

    @Override
    public String[] columnTitles() {
        return columnTitles;
    }

    @Override
    public boolean includeNullColumns() {
        return includeNullColumns;
    }

    @Override
    public boolean numberedRows() {
        return numberedRows;
    }

    @Override
    public String numberedRowsHeader() {
        return numberedRowsHeader;
    }

    @Override
    public boolean numberedColumns() {
        return numberedColumns;
    }

    @Override
    public String numberedColumnsHeader() {
        return numberedColumnsHeader;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
