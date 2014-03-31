package com.tngtech.jgiven.impl.util;

public class WordUtil {

    /**
     * Returns the given text with the first letter in upper case
     * 
     * <h2>Examples:</h2>
     * <pre>
     * capitalize("hi") == "Hi"
     * capitalize("Hi") == "Hi"
     * capitalize("hi there") == "hi there"
     * capitalize("") == ""
     * capitalize(null) == null
     * </pre>
     * @param text the text to capitalize
     * @return text with the first letter in upper case
     */
    public static String capitalize( String text ) {
        if( text == null || text.isEmpty() )
            return text;
        return text.substring( 0, 1 ).toUpperCase().concat( text.substring( 1, text.length() ) );
    }

}
