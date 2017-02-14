package com.tngtech.jgiven.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.tngtech.jgiven.exception.JGivenWrongUsageException;

public class DateFormatterTest {

    Locale defaultLocale;
    DateFormatter formatter;
    SimpleDateFormat sdf;

    @Before
    public void setup() {
        // Save current default locale
        defaultLocale = Locale.getDefault();

        formatter = new DateFormatter();
        sdf = new SimpleDateFormat( "dd/MM/yy HH:mm:ss" );
    }

    @After
    public void tearDown() {
        // Set initial default locale
        Locale.setDefault( defaultLocale );
    }

    @Test
    public void testFormat() throws Exception {
        Locale.setDefault( Locale.ENGLISH );

        Date now = sdf.parse( "21/01/2017 23:50:14" );
        String expected = "Sat, 21 Jan 2017 23:50:14";
        assertThat( formatter.format( now, new String[] { "EEE, d MMM yyyy HH:mm:ss" } ) ).isEqualTo( expected );
    }

    @Test
    public void testFormat_SpecifyLocale() throws Exception {
        Locale.setDefault( Locale.ENGLISH );

        Date now = sdf.parse( "21/01/2017 23:50:14" );
        Locale locale = Locale.FRANCE;
        String expected = "sam., 21 janv. 2017 23:50:14";
        assertThat( formatter.format( now, new String[] { "EEE, d MMM yyyy HH:mm:ss", locale.getLanguage() } ) ).isEqualTo( expected );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void testFormat_DateFormatPatternArgumentIsMissing() throws Exception {
        Date now = sdf.parse( "21/01/2017 23:50:14" );
        formatter.format( now, new String[] {} );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void testFormat_DateFormatPatternArgumentIsInvalid() throws Exception {
        Date now = sdf.parse( "21/01/2017 23:50:14" );
        formatter.format( now, new String[] { "XXXXXXXX" } );
    }

}
