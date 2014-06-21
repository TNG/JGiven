package com.tngtech.jgiven.junit;

import static com.google.common.collect.Maps.immutableEntry;
import static org.assertj.core.error.ShouldContainExactly.shouldContainExactly;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import junitparams.internal.InvokeParameterisedMethod;

import org.assertj.core.api.MapAssert;
import org.assertj.core.internal.Failures;
import org.assertj.core.internal.Maps;
import org.assertj.core.internal.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderFrameworkMethod;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;

@RunWith( DataProviderRunner.class )
public class ScenarioTestRuleTest {

    @DataProvider
    public static Object[][] methodTestData() throws Exception {
        return new Object[][] {
            // Normal JUnit test
            { emptyStatement(), anyFrameworkMethod(), new Object(), },

            // junit-dataprovider test
            { emptyStatement(), dataProviderFrameworkMethod( twoParamsMethod(), "arg1", 2 ), new Object(),
                new Map.Entry<?, ?>[] { immutableEntry( "s", "arg1" ), immutableEntry( "i", 2 ) } },

            // junitparams test
            { junitParamsStatement( twoParamsMethod(), "arg1, 2" ), anyFrameworkMethod(), new Object(),
                new Map.Entry<?, ?>[] { immutableEntry( "s", "arg1" ), immutableEntry( "i", 2 ) } },

            // @Parameterized test
            { emptyStatement(), anyFrameworkMethod(), new ParameterizedTest( "test1", 4, false ),
                new Map.Entry<?, ?>[] { immutableEntry( "s", "test1" ), immutableEntry( "i", 4 ),
                    immutableEntry( "b", false ) } }, };
    }

    @Test
    @UseDataProvider( "methodTestData" )
    public void testParseMethodName( Statement statement, FrameworkMethod testMethod, Object target,
            Map.Entry<?, ?>[] expected ) {

        LinkedHashMap<String, ?> result = ScenarioExecutionRule.getMethodArguments( statement, testMethod, target );
        assertThat( result ).containsExactly( expected );
    }

    // -- helper methods -----------------------------------------------------------------------------------------------

    private static Statement emptyStatement() {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {}
        };
    }

    private static FrameworkMethod anyFrameworkMethod() throws Exception {
        return new FrameworkMethod( twoParamsMethod() );
    }

    private static Object dataProviderFrameworkMethod( Method method, Object... args ) {
        return new DataProviderFrameworkMethod( method, 1, args );
    }

    private static Object junitParamsStatement( Method method, String args ) {
        return new InvokeParameterisedMethod( new FrameworkMethod( method ), ScenarioTestRuleTest.class, args, 1 );
    }

    private static Method twoParamsMethod() throws Exception {
        return getMethod( "testMethodWithTwoParams", String.class, int.class );
    }

    private static Method getMethod( String methodName, Class<?>... types ) throws Exception {
        return ScenarioTestRuleTest.class.getDeclaredMethod( methodName, types );
    }

    private static <K, V> LinkedHashMapAssert<K, V> assertThat( LinkedHashMap<K, V> actual ) {
        return new LinkedHashMapAssert<K, V>( actual );
    }

    private static class LinkedHashMapAssert<K, V> extends MapAssert<K, V> {
        private final Objects objects = Objects.instance();
        private final Maps maps = Maps.instance();
        private final Failures failures = Failures.instance();

        protected LinkedHashMapAssert( LinkedHashMap<K, V> actual ) {
            super( actual );
        }

        public LinkedHashMapAssert<K, V> containsExactly( Map.Entry<?, ?>... entries ) {
            if( entries == null ) {
                throw new NullPointerException( "The array of entries to look for should not be null" );
            }
            objects.assertNotNull( info, actual );

            // if both actual and values are empty, then assertion passes.
            if( actual.isEmpty() && entries.length == 0 ) {
                return this;
            }
            maps.assertHasSameSizeAs( info, actual, entries );

            int idx = 0;
            for( Iterator<Entry<K, V>> actualIterator = actual.entrySet().iterator(); actualIterator.hasNext(); ) {
                Entry<K, V> entry = actualIterator.next();
                Entry<?, ?> expected = entries[idx];

                if( !entry.equals( expected ) ) {
                    throw failures.failure( info, shouldContainExactly( entry, expected, idx ) );
                }
                idx++;
            }
            return this;
        }
    }

    // -- mocks --------------------------------------------------------------------------------------------------------

    public void testMethodWithTwoParams( String s, int i ) {}

    @RunWith( Parameterized.class )
    public static class ParameterizedTest {
        private final static String S = "static";

        private final Object o = new Object();
        private final String s;
        private final double d = 5.0;
        private final int i;
        private final Boolean b;

        public ParameterizedTest( String s, int i, Boolean b ) {
            this.s = s;
            this.i = i;
            this.b = b;
        }
    }

}
