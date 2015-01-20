package com.tngtech.jgiven.attachment;

import javax.xml.bind.DatatypeConverter;

/**
 * Represents an binary attachment
 */
public class BinaryAttachment implements Attachment {

    private final String base64;
    private final String mimeType;

    private BinaryAttachment( String base64, String mimeType ) {
        this.base64 = base64;
        this.mimeType = mimeType;
    }

    public static BinaryAttachment fromBase64PngImage( String base64 ) {
        return fromBase64( base64, "image/png" );
    }

    public static BinaryAttachment fromBase64( String base64, String mimeType ) {
        return new BinaryAttachment( base64, mimeType );
    }

    public static BinaryAttachment fromBytes( byte[] bytes, String mimeType ) {
        return new BinaryAttachment( DatatypeConverter.printBase64Binary( bytes ), mimeType );
    }

    @Override
    public String asString() {
        return base64;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }
}
