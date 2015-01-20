package com.tngtech.jgiven.report.model;

public class AttachmentModel {
    private String value;
    private String mimeType;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType( String mimeType ) {
        this.mimeType = mimeType;
    }
}
