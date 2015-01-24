package com.tngtech.jgiven.attachment;

import static com.tngtech.jgiven.attachment.MediaType.Type.*;

/**
 * Represents a <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">Media Type</a>.

 * @since 0.7.0
 */
public class MediaType {

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
        public Type fromString( String string ) {
            for( Type type : values() ) {
                if( type.value.equalsIgnoreCase( string ) ) {
                    return type;
                }
            }
            throw new IllegalArgumentException( "Unknown type " + string );
        }

    }

    public static final MediaType PNG = image( "png" );
    public static final MediaType PLAIN_TEXT = text( "plain" );

    private final Type type;
    private final String subType;
    private final boolean binary;

    /**
     * Creates a new MediaType
     * @param type the type
     * @param subType the subtype
     * @param binary whether or not content of this media type is binary. If {@code true}, the 
     *                 content will be encoded as Base64 when stored in the JSON model.
     */
    public MediaType( Type type, String subType, boolean binary ) {
        this.type = type;
        this.subType = subType;
        this.binary = binary;
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

    public String asString() {
        return type.value + "/" + subType;
    }

    /**
     * Creates a binary image media type with the given subtype.
     */
    public static MediaType image( String subType ) {
        return new MediaType( IMAGE, subType, true );
    }

    /**
     * Creates a binary video media type with the given subtype.
     */
    public static MediaType video( String subType ) {
        return new MediaType( VIDEO, subType, true );
    }

    /**
     * Creates a binary audio media type with the given subtype.
     */
    public static MediaType audio( String subType ) {
        return new MediaType( AUDIO, subType, true );
    }

    /**
     * Creates a non-binary text media type with the given subtype.
     */
    public static MediaType text( String subType ) {
        return new MediaType( TEXT, subType, false );
    }

}
