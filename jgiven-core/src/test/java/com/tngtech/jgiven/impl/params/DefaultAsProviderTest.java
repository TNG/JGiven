package com.tngtech.jgiven.impl.params;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith( DataProviderRunner.class )
public class DefaultAsProviderTest {

    @DataProvider( {
            "TEST, TEST",
            "fooBar, foo bar",
            "foo, foo",
            "FooBar, foo bar",
            "Foo_Bar, Foo Bar",
            "foo_bar_Baz, foo bar Baz",
            "foo_bar, foo bar"
    } )
    @Test
    public void test_method_to_readable_text( String methodName, String expectedText ) {
        DefaultAsProvider provider = new DefaultAsProvider();
        assertThat( provider.methodNameToReadableText( methodName ) ).isEqualTo( expectedText );
    }
}
