package com.tngtech.jgiven.attachment;

import java.io.*;
import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.impl.util.ResourceUtil;

/**
 * Represents an attachment of a step. 
 * Attachments must be representable as a String so that it can be stored as JSON.
 * For binary attachments this means that they have to be encoded with Base64.
 * In addition, attachments must have a media type so that reporters know
 * how to present an attachment.
 * 
 * @since 0.7.0
 */
public class Attachment {

    /**
     * An optional title. 
     * Can be {@code null}.
     */
    private String title;

    /**
     * An optional filename.
     * Can be {@code null}.
     */
    private String fileName;

    /**
     * The content of the attachment.
     * In case the media type is binary, this is a Base64 encoded string
     * Is never {@code null}
     */
    private final String content;

    /**
     * The media type of the attachment
     * Is never {@code null}
     */
    private final MediaType mediaType;

    /**
     * Whether this attachment should be directly shown showDirectly in the scenario.
     * Can be {@code null} to save bytes in JSON file
     */
    private Boolean showDirectly;

    /**
     * Convenience constructor, where title is set to {@code null}
     */
    protected Attachment( String content, MediaType mediaType ) {
        this( content, mediaType, null );
    }

    /**
     * Creates a new instance of this Attachment
     * @param content the content of this attachment. In case of a binary attachment, this must be
     *                Base64 encoded. Must not be {@code null}
     * @param mediaType the mediaType. Must not be {@code null}
     * @param title an optional title, may be {@code null}
     */
    protected Attachment( String content, MediaType mediaType, String title ) {
        if( mediaType == null ) {
            throw new IllegalArgumentException( "MediaType must not be null" );
        }

        if( content == null ) {
            throw new IllegalArgumentException( "Content must not be null" );
        }

        this.content = content;
        this.mediaType = mediaType;
        this.title = title;
    }

    /**
     * The content of the attachment represented as a string. 
     * Binary attachments must be encoded in Base64 format.
     */
    public String getContent() {
        return content;
    }

    /**
     * The type of the attachment. 
     * It depends on the reporter how this information is used.
     */
    public MediaType getMediaType() {
        return mediaType;
    }

    /**
     * An optional title of the attachment.
     * The title can be used by reporters, e.g. as a tooltip.
     * Can be {@code null}.
     */
    public String getTitle() {
        return title;
    }

    /**
     * An optional filename for the attachment.
     * Can be {@code null}.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * An optional filename for the attachment without the file type suffix.
     * The name can be used by reporters.  
     * 
     * @since 0.9.3
     */
    public Attachment withFileName( String fileName ) {
        this.fileName = fileName;
        return this;
    }

    /**
     * Sets the title and returns {@code this}
     */
    public Attachment withTitle( String title ) {
        this.title = title;
        return this;
    }

    /**
     * Directly shows this attachment in the report.
     * By default, the attachment is not directly shown in the scenario, 
     * but referenced by a link.
     * <p>
     * Note: This currently works only for images!
     * </p>
     * @throws com.tngtech.jgiven.exception.JGivenWrongUsageException if the attachment is not an image
     * @return {@code this}
     * @since 0.8.2
     */
    public Attachment showDirectly() {
        if( !this.getMediaType().isImage() ) {
            throw new JGivenWrongUsageException( "Only images can be directly shown" );
        }
        this.showDirectly = true;
        return this;
    }

    /**
     * Creates an attachment from a given array of bytes.
     * The bytes will be Base64 encoded.
     * @throws java.lang.IllegalArgumentException if mediaType is not binary
     */
    public static Attachment fromBinaryBytes( byte[] bytes, MediaType mediaType ) {
        if( !mediaType.isBinary() ) {
            throw new IllegalArgumentException( "MediaType must be binary" );
        }
        return new Attachment( DatatypeConverter.printBase64Binary( bytes ), mediaType, null );
    }

    /**
     * Creates an attachment from a binary input stream.
     * The content of the stream will be transformed into a Base64 encoded string
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is not binary
     */
    public static Attachment fromBinaryInputStream( InputStream inputStream, MediaType mediaType ) throws IOException {
        return fromBinaryBytes( ByteStreams.toByteArray( inputStream ), mediaType );
    }

    /**
     * Creates an attachment from the given binary file {@code file}.
     * The content of the file will be transformed into a Base64 encoded string.
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is not binary
     */
    public static Attachment fromBinaryFile( File file, MediaType mediaType ) throws IOException {
        FileInputStream stream = new FileInputStream( file );
        try {
            return fromBinaryInputStream( stream, mediaType );
        } finally {
            ResourceUtil.close( stream );
        }
    }

    /**
     * Creates a non-binary attachment from the given file.
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is binary
     * @deprecated use fromTextFile without charSet with a mediaType that has a specified charSet 
     */
    @Deprecated
    public static Attachment fromTextFile( File file, MediaType mediaType, Charset charSet ) throws IOException {
        return fromText( Files.toString( file, charSet ), mediaType );
    }

    /**
     * Creates a non-binary attachment from the given file.
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is either binary or has no specified charset
     */
    public static Attachment fromTextFile( File file, MediaType mediaType ) throws IOException {
        return fromText( Files.toString( file, mediaType.getCharset() ), mediaType );
    }

    /**
     * Creates a non-binary attachment from the given file.
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is binary
     * @deprecated use fromTextInputStream without charSet with a mediaType that has a specified charSet 
     */
    @Deprecated
    public static Attachment fromTextInputStream( InputStream inputStream, MediaType mediaType, Charset charset ) throws IOException {
        return fromText( CharStreams.toString( new InputStreamReader( inputStream, charset ) ), mediaType );
    }

    /**
     * Creates a non-binary attachment from the given file.
     * @throws IOException if an I/O error occurs
     * @throws java.lang.IllegalArgumentException if mediaType is either binary or has no specified charset
     */
    public static Attachment fromTextInputStream( InputStream inputStream, MediaType mediaType ) throws IOException {
        return fromText( CharStreams.toString( new InputStreamReader( inputStream, mediaType.getCharset() ) ), mediaType );
    }

    /**
     * Equivalent to {@link com.tngtech.jgiven.attachment.Attachment#Attachment(String, MediaType)}
     * @throws java.lang.IllegalArgumentException if mediaType is binary
     */
    public static Attachment fromText( String content, MediaType mediaType ) {
        if( mediaType.isBinary() ) {
            throw new IllegalArgumentException( "MediaType must not be binary" );
        }
        return new Attachment( content, mediaType );
    }

    /**
     * Creates a text attachment with the given content with media type text/plain.
     * 
     * @param content the content of the attachment
     */
    public static Attachment plainText( String content ) {
        return fromText( content, MediaType.PLAIN_TEXT_UTF_8 );
    }

    /**
     * Creates a text attachment with the given content with media type text/xml.
     *
     * @param content the content of the attachment
     */
    public static Attachment xml( String content ) {
        return fromText( content, MediaType.XML_UTF_8 );
    }

    /**
     * Creates a text attachment with the given content with media type application/json.
     *
     * @param content the content of the attachment
     */
    public static Attachment json( String content ) {
        return fromText( content, MediaType.JSON_UTF_8 );
    }

    /**
     * Equivalent to {@link com.tngtech.jgiven.attachment.Attachment#Attachment(String, MediaType)}
     * @throws java.lang.IllegalArgumentException if mediaType is not binary
     */
    public static Attachment fromBase64( String base64encodedContent, MediaType mediaType ) {
        if( !mediaType.isBinary() ) {
            throw new IllegalArgumentException( "MediaType must be binary" );
        }
        return new Attachment( base64encodedContent, mediaType );
    }

    /**
     * Whether this attachment is shown showDirectly or not
     * @see com.tngtech.jgiven.attachment.Attachment#showDirectly
     * @since 0.8.2
     */
    public boolean getShowDirectly() {
        return showDirectly == null ? false : showDirectly;
    }
}
