package com.tngtech.jgiven.report.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;

@RunWith( DataProviderRunner.class )
public class DurationFormatterTest {

    @Test
    @DataProvider( value = {
        "678, 0.00 ms",
        "345678, 0.35 ms",
        "12345678, 12.35 ms",
        "123456789, 123.46 ms",
        "500006789, 500.01 ms",
        "1500006789, 1.50 s",
        "15000067890, 15.00 s",
        "60000067890, 1.00 min",
    } )
    public void test( long nanos, String expectedResult ) {
        assertThat( DurationFormatter.format( nanos ) ).isEqualTo( expectedResult );
    }
}
