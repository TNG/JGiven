package com.tngtech.jgiven.impl.util;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

public class PrintWriterUtil {

    public static PrintWriter getPrintWriter( File file ) {
        try {
            return new PrintWriter( file, Charsets.UTF_8.name() );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }
    }

    public static PrintWriter getPrintWriter( OutputStream outputStream ) {
        try {
            return new PrintWriter( new OutputStreamWriter( outputStream, Charsets.UTF_8.name() ) );
        } catch( UnsupportedEncodingException e ) {
            throw Throwables.propagate( e );
        }
    }

}
