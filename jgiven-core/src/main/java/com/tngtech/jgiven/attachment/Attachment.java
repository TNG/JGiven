package com.tngtech.jgiven.attachment;

/**
 * Represents an attachment of a step
 */
public interface Attachment {
    String asString();

    String getMimeType();
}
