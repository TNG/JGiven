package com.tngtech.jgiven.report.util;

import java.util.Formatter;
import java.util.Locale;

public class DurationFormatter {

    static class Duration {
        String unit;
        double value;
    }

    public static String format( long durationInNanos ) {
        Formatter usFormatter = new Formatter( Locale.US );

        Duration duration = getHumanReadableDuration( durationInNanos );

        try {
            return usFormatter.format( "%.2f ", duration.value ) + duration.unit;
        } finally {
            usFormatter.close();
        }
    }

    private static Duration getHumanReadableDuration( double durationInNanos ) {
        Duration result = new Duration();
        result.value = durationInNanos / 1000000;
        result.unit = "ms";

        if( result.value < 1000 ) {
            return result;
        }

        result.value /= 1000;
        result.unit = "s";

        if( result.value < 60 ) {
            return result;
        }

        result.value /= 60;
        result.unit = "min";

        return result;
    }

}
