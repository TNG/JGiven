package com.tngtech.jgiven.report.model;

public class AttachmentModel {
    private String title;
    private String value;
    private String mediaType;
    private boolean binary;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType( String mimeType ) {
        this.mediaType = mimeType;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIsBinary( boolean isBinary ) {
        this.binary = isBinary;
    }

    public boolean isBinary() {
        return binary;
    }
}
