package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.format.table.DefaultTableFormatter;
import com.tngtech.jgiven.format.table.FieldBasedRowFormatter;
import com.tngtech.jgiven.format.table.RowFormatterFactory;
import com.tngtech.jgiven.format.table.TableFormatter;
import com.tngtech.jgiven.impl.util.AnnotationUtil;

public class TableAnnotation implements Table {

    HeaderType header = HeaderType.HORIZONTAL;
    boolean transpose = false;
    boolean includeNullColumns = false;
    String[] excludeFields = {};
    String[] includeFields = {};
    String[] columnTitles = {};
    boolean numberedRows = false;
    boolean numberedColumns = false;
    String numberedRowsHeader = AnnotationUtil.ABSENT;
    String numberedColumnsHeader = AnnotationUtil.ABSENT;
    Class<DefaultTableFormatter> formatter = DefaultTableFormatter.class;
    Class<? extends RowFormatterFactory> objectRowFormatter = FieldBasedRowFormatter.Factory.class;
    ObjectFormatting objectFormatting = ObjectFormatting.FIELDS;

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
    public Class<? extends TableFormatter> formatter() {
        return formatter;
    }

    @Override
    public ObjectFormatting objectFormatting() {
        return objectFormatting;
    }

    @Override
    public Class<? extends RowFormatterFactory> rowFormatter() {
        return objectRowFormatter;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
