package com.tngtech.jgiven.impl.util;

import java.io.*;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.tngtech.jgiven.config.ConfigValue;

public class PrintWriterUtil {
    public static PrintWriter getPrintWriter( File file ) {
        try {
            return new PrintWriter( file, Charsets.UTF_8.name() );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        }
    }

    public static PrintWriter getPrintWriter( OutputStream outputStream, ConfigValue colorConfig ) {
        OutputStream wrappedStream = outputStream;

        if( colorConfig == ConfigValue.TRUE || colorConfig == ConfigValue.AUTO ) {
            wrappedStream = AnsiUtil.wrapOutputStream( outputStream, colorConfig == ConfigValue.AUTO );
        }

        try {
            return new PrintWriter( new OutputStreamWriter( wrappedStream, Charsets.UTF_8.name() ) );
        } catch( UnsupportedEncodingException e ) {
            throw Throwables.propagate( e );
        }
    }

}
