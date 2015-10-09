package com.tngtech.jgiven.impl.util;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.report.model.NamedArgument;

@RunWith( DataProviderRunner.class )
public class ParameterNameUtilTest {

    @DataProvider
    @SuppressWarnings( { "unchecked", "boxing" } )
    public static Object[][] dataProviderMapArgumentsWithParameterNamesOf() throws Exception {
        // @formatter:off
        return new Object[][] {
            { methodWithNoArgs(),         emptyList(),                 emptyList() },
            { methodWithThreeArgs(),      asList( "test1", 1, false ), asList( na( "s", "test1" ), na( "i", 1 ),   na( "b", false ) ) },
            { constructorWithThreeArgs(), asList( "test2", 1.0, 7L ),  asList( na( "s", "test2" ), na( "d", 1.0 ), na( "l", 7L )    ) },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider( "dataProviderMapArgumentsWithParameterNamesOf" )
    public void testMapArgumentsWithParameterNamesOf( AccessibleObject contructorOrMethod, List<Object> arguments,
            List<NamedArgument> expected ) {
        // When:
        List<NamedArgument> result = ParameterNameUtil.mapArgumentsWithParameterNames( contructorOrMethod, arguments );

        // Then:
        assertThat( result ).containsExactly( expected.toArray( new NamedArgument[0] ) );
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    private static NamedArgument na( String name, Object value ) {
        return new NamedArgument( name, value );
    }

    private static Method methodWithNoArgs() throws Exception {
        return ParameterNameUtilTest.class.getDeclaredMethod( "methodWithNoArgs" );
    }

    private static Method methodWithThreeArgs() throws Exception {
        return ParameterNameUtilTest.class.getDeclaredMethod( "methodWithThreeArgs", String.class, int.class, Boolean.class );
    }

    private static Constructor<Clazz> constructorWithThreeArgs() throws Exception {
        return Clazz.class.getDeclaredConstructor( String.class, double.class, Long.class );
    }

    private static void methodWithThreeArgs( String s, int i, Boolean b ) throws Exception {}

    private static class Clazz {
        private Clazz( String s, double d, Long l ) {}
    }
}
