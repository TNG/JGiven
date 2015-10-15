package com.tngtech.jgiven.report.model;

public class AttachmentModel {
    private String title;
    private String value;
    private String fileName;
    private String mediaType;
    private boolean binary;

    /**
     * Shows the attachment showDirectly in the scenario
     * Can be {@code null} to save bytes in the JSON file
     * @since 0.8.2
     */
    private Boolean showDirectly;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName( String fileName ) {
        this.fileName = fileName;
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

    public void setShowDirectly( boolean showDirectly ) {
        this.showDirectly = showDirectly ? true : null;
    }

    public boolean isShowDirectly() {
        return showDirectly != null && showDirectly == true;
    }

}
