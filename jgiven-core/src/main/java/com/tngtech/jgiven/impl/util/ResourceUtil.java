package com.tngtech.jgiven.impl.util;

import java.io.Closeable;

import com.google.common.base.Throwables;

public class ResourceUtil {
    public static void close( Closeable... closeables ) {
        Exception t = null;
        for( Closeable c : closeables ) {
            try {
                if( c != null ) {
                    c.close();
                }
            } catch( Exception e ) {
                t = e;
            }
        }
        if( t != null ) {
            throw Throwables.propagate( t );
        }
    }
}
