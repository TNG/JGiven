package com.tngtech.jgiven.attachment;

/**
 * Represents a text attachment
 */
public class TextAttachment implements Attachment {

    private final String content;

    public TextAttachment( String content ) {
        this.content = content;
    }

    @Override
    public String asString() {
        return content;
    }

    @Override
    public String getMimeType() {
        return "text/plain";
    }
}
