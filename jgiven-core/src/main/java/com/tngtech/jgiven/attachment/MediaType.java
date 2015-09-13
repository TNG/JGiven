package com.tngtech.jgiven.attachment;

import static com.tngtech.jgiven.attachment.MediaType.Type.*;

import java.nio.charset.Charset;

import com.tngtech.jgiven.impl.util.ApiUtil;

/**
 * Represents a (simplified) <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">Media Type</a>.

 * @since 0.7.0
 */
public class MediaType {

    private static final Charset UTF_8 = Charset.forName( "utf8" );

    /**
     * Represents the type of a Media Type
     */
    public static enum Type {
        APPLICATION( "application" ),
        AUDIO( "audio" ),
        IMAGE( "image" ),
        TEXT( "text" ),
        VIDEO( "video" );

        private final String value;

        Type( String value ) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        /**
         * Get the type from a given string
         */
        public static Type fromString( String string ) {
            for( Type type : values() ) {
                if( type.value.equalsIgnoreCase( string ) ) {
                    return type;
                }
            }
            throw new IllegalArgumentException( "Unknown type " + string );
        }

    }

    /**
     * image/gif
     */
    public static final MediaType GIF = image( "gif" );

    /**
     * image/png
     */
    public static final MediaType PNG = image( "png" );

    /**
     * image/jpeg
     */
    public static final MediaType JPEG = image( "jpeg" );

    /**
     * image/svg+xml
     */
    public static final MediaType SVG_UTF_8 = imageUtf8( "svg+xml" );

    /**
     * text/plain
     * @deprecated use PLAIN_TEXT_UTF_8 instead
     */
    @Deprecated
    public static final MediaType PLAIN_TEXT = text( "plain" );

    /**
     * text/plain
     */
    public static final MediaType PLAIN_TEXT_UTF_8 = textUtf8( "plain" );

    /**
     * application/json
     */
    public static final MediaType JSON_UTF_8 = applicationUtf8( "json" );

    /**
     * application/xml
     */
    public static final MediaType APPLICATION_XML_UTF_8 = applicationUtf8( "xml" );

    /**
     * text/xml
     */
    public static final MediaType XML_UTF_8 = textUtf8( "xml" );

    private final Type type;
    private final String subType;
    private final boolean binary;

    /**
     * An optional charset, can be {@code null}
     */
    private final Charset charset;

    /**
     * Creates a new MediaType
     * @param type the type
     * @param subType the subtype
     * @param binary whether or not content of this media type is binary. If {@code true}, the 
     *                 content will be encoded as Base64 when stored in the JSON model.
     * @throws com.tngtech.jgiven.exception.JGivenWrongUsageException if any of the parameters is {@code null}
     * @deprecated please use static factory methods instead 
     */
    @Deprecated
    public MediaType( Type type, String subType, boolean binary ) {
        this.type = ApiUtil.notNull( type, "type must not be null" );
        this.subType = ApiUtil.notNull( subType, "subType must not be null" );
        this.binary = binary;
        this.charset = null;
    }

    private MediaType( Type type, String subType, Charset charset ) {
        this.type = ApiUtil.notNull( type, "type must not be null" );
        this.subType = ApiUtil.notNull( subType, "subType must not be null" );
        this.charset = ApiUtil.notNull( charset, "charset must not be null" );
        this.binary = false;
    }

    /**
     * The type of the Media Type.
     */
    public Type getType() {
        return type;
    }

    /**
     * The subtype of the Media Type.
     */
    public String getSubType() {
        return subType;
    }

    /**
     * Whether this media type is binary or not.
     */
    public boolean isBinary() {
        return binary;
    }

    public boolean isImage() {
        return type == IMAGE;
    }

    /**
     * @return the charset of this media type if one is specified
     * @throws java.lang.IllegalArgumentException if no charset is specified
     */
    public Charset getCharset() {
        if( charset == null ) {
            throw new IllegalArgumentException( "No charset is specified for media type " + this );
        }
        return charset;
    }

    public String asString() {
        return type.value + "/" + subType;
    }

    /**
     * Creates a binary media type with the given type and subtype
     * @throws com.tngtech.jgiven.exception.JGivenWrongUsageException if any of the given arguments is {@code null}
     */
    public static MediaType binary( MediaType.Type type, String subType ) {
        return new MediaType( type, subType, true );
    }

    /**
     * Creates a non-binary media type with the given type, subtype, and charSet
     * @throws com.tngtech.jgiven.exception.JGivenWrongUsageException if any of the given arguments is {@code null}
     */
    public static MediaType nonBinary( MediaType.Type type, String subType, Charset charSet ) {
        ApiUtil.notNull( charSet, "charset must not be null" );
        return new MediaType( type, subType, charSet );
    }

    /**
     * Creates a non-binary media type with the given type, subtype, and UTF-8 encoding
     * @throws com.tngtech.jgiven.exception.JGivenWrongUsageException if any of the given arguments is {@code null}
     */
    public static MediaType nonBinaryUtf8( MediaType.Type type, String subType ) {
        return new MediaType( type, subType, UTF_8 );
    }

    /**
     * Creates a binary image media type with the given subtype.
     */
    public static MediaType image( String subType ) {
        return binary( IMAGE, subType );
    }

    /**
     * Creates a textual image media type with the given subtype and UTF-8 encoding
     */
    public static MediaType imageUtf8( String subType ) {
        return nonBinaryUtf8( IMAGE, subType );
    }

    /**
     * Creates a binary application media type with the given subtype.
     */
    public static MediaType application( String subType ) {
        return binary( APPLICATION, subType );
    }

    /**
     * Creates a textual application media type with the given subtype and UTF-8 encoding
     */
    public static MediaType applicationUtf8( String subType ) {
        return nonBinaryUtf8( APPLICATION, subType );
    }

    /**
     * Creates a binary video media type with the given subtype.
     */
    public static MediaType video( String subType ) {
        return binary( VIDEO, subType );
    }

    /**
     * Creates a binary audio media type with the given subtype.
     */
    public static MediaType audio( String subType ) {
        return binary( AUDIO, subType );
    }

    /**
     * Creates a non-binary text media type with the given subtype, but without a specified encoding
     * @deprecated use a method with a specific encoding
     */
    @Deprecated
    public static MediaType text( String subType ) {
        return new MediaType( TEXT, subType, false );
    }

    /**
     * Creates a non-binary text media type with the given subtype and a specified encoding
     */
    public static MediaType text( String subType, Charset charset ) {
        return nonBinary( TEXT, subType, charset );
    }

    /**
     * Creates a non-binary text media type with the given subtype.
     */
    public static MediaType textUtf8( String subType ) {
        return text( subType, UTF_8 );
    }

}
