package com.tngtech.jgiven.impl.util;

public class ThrowableUtil {
    public static boolean isAssumptionException(Throwable t) {
        return t.getClass().getName().equals( "org.junit.AssumptionViolatedException" )
            || t.getClass().getName().equals( "org.testng.SkipException");
    }
}
