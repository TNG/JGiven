package com.tngtech.jgiven.impl.util;

import static com.tngtech.assertj.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import org.assertj.core.data.MapEntry;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith( DataProviderRunner.class )
public class ScenarioUtilTest {

    @DataProvider
    public static Object[][] dataProviderMapArgumentsWithParameterNamesOf() throws Exception {
        // @formatter:off
        return new Object[][] {
            { methodWithNoArgs(), null, new MapEntry[0] },

            { methodWithThreeArgs(),      array( "test1", 1, false ), array( entry( "s", "test1" ), entry( "i", 1 ),   entry( "b", false ) ) },
            { constructorWithThreeArgs(), array( "test2", 1.0, 7L ),  array( entry( "s", "test2" ), entry( "d", 1.0 ), entry( "l", 7L )    ) },
        };
        // @formatter:on
    }

    @Test
    @UseDataProvider( "dataProviderMapArgumentsWithParameterNamesOf" )
    public void testMapArgumentsWithParameterNamesOf( AccessibleObject contructorOrMethod, Object[] arguments, MapEntry[] expected ) {
        // When:
        LinkedHashMap<String, ?> result = ScenarioUtil.mapArgumentsWithParameterNamesOf( contructorOrMethod, arguments );

        // Then:
        assertThat( result ).containsExactly( expected );
    }

    // -- helper methods -----------------------------------------------------------------------------------------------
    private static <T> T[] array( T... ts ) {
        return ts;
    }

    private static Method methodWithNoArgs() throws Exception {
        return ScenarioUtilTest.class.getDeclaredMethod( "methodWithNoArgs" );
    }

    private static Method methodWithThreeArgs() throws Exception {
        return ScenarioUtilTest.class.getDeclaredMethod( "methodWithThreeArgs", String.class, int.class, Boolean.class );
    }

    private static Constructor<Clazz> constructorWithThreeArgs() throws Exception {
        return Clazz.class.getDeclaredConstructor( String.class, double.class, Long.class );
    }

    private static void methodWithThreeArgs( String s, int i, Boolean b ) throws Exception {}

    private static class Clazz {
        private Clazz( String s, double d, Long l ) {}
    }
}
