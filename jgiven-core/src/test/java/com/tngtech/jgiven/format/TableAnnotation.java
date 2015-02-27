package com.tngtech.jgiven.format;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.Table;

public class TableAnnotation implements Table {

    HeaderType header = HeaderType.HORIZONTAL;
    boolean transpose = false;
    String[] excludeFields = {};
    String[] includeFields = {};
    String[] columnTitles = {};

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
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
