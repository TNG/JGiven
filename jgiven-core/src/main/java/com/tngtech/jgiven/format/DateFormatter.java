package com.tngtech.jgiven.format;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tngtech.jgiven.exception.JGivenWrongUsageException;

/**
 * General formatter to format {@link Date} values.
 *
 * <p>
 * This formatter simply delegates to a {@link SimpleDateFormat}.<br>
 * </p>
 * @since 0.15.0
 *
 */
public class DateFormatter implements ArgumentFormatter<Date> {

    /**
     * A {@link SimpleDateFormat} pattern is expected as first argument.<br>
     * An optional second argument can be set to specify a locale as an ISO 639 language code.<br>
     */
    @Override
    public String format( Date date, String... args ) {
        if( date == null ) {
            return "";
        }

        if( args.length == 0 ) {
            throw new JGivenWrongUsageException( String.format( "A SimpleDateFormat pattern is expected as first argument" ) );
        }

        String pattern = args[0];
        Locale locale = Locale.getDefault();
        if( args.length > 1 ) {
            locale = new Locale( args[1] );
        }

        SimpleDateFormat sdf;
        try {
            sdf = new SimpleDateFormat( pattern, locale );
        } catch( IllegalArgumentException e ) {
            throw new JGivenWrongUsageException( String.format( "A valid SimpleDateFormat pattern is expected (was '%s')", pattern ) );
        }

        return sdf.format( date );
    }

}
