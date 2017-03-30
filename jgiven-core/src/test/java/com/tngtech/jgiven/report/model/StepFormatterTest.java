package com.tngtech.jgiven.report.model;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.NotFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.report.model.StepFormatter.ArgumentFormatting;

import javax.lang.model.element.Name;

@RunWith( DataProviderRunner.class )
public class StepFormatterTest {

    @DataProvider
    public static Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "a",             asList(),                 asList(),           "a" },
                { "a b",           asList(),                 asList(),           "a b" },
                { " a ",           asList(),                 asList(),           " a " },
                { "",              asList( "strA" ),         asList( "a" ),      "a" },
                { "foo",           asList( "strA" ),         asList( "a" ),      "foo a" },
                { "",              asList( "strA", "strB" ), asList( "a", "b" ), "a b" },
                { "$",             asList( "strA" ),         asList( "a" ),      "a" },
                { "$foo",          asList( "strA" ),         asList( "a" ),      "a" },
                { "foo $",         asList( "strA" ),         asList( "a" ),      "foo a" },
                { "$ foo",         asList( "strA" ),         asList( "a" ),      "a foo" },
                { "foo $ foo",     asList( "strA" ),         asList( "a" ),      "foo a foo" },
                { "$ $",           asList( "strA", "strB" ), asList( "a", "b" ), "a b" },
                { "$foo bar$",     asList( "strA", "strB" ), asList( "a", "b" ), "a bar b" },
                { "foo$bar$baz x", asList( "strA", "strB" ), asList( "a", "b" ), "foo a b x" },
                { "$d foo",        asList( "int5" ),         asList( 5 ),        "5 foo" },
                { "$1 foo $1",     asList( "strA" ),         asList( "a" ),      "a foo a" },
                { "$2 $1",         asList( "strA", "strB" ), asList("a", "b"),   "b a" },
                { "$]",            asList( "strA"),          asList( "a" ),      "a ]" },
                { "foo $]",        asList( "strA" ),         asList( "a" ),      "foo a ]" },
        };
        // @formatter:on

    }

    @Test
    @UseDataProvider( "testCases" )
    public void formatter_should_handle_dollars_correctly( String value, List<String> parameterNames, List<Object> parameterValues,
            String expectedValue ) {
        testFormatter( value, parameterNames, parameterValues, null, null, expectedValue );

    }

    static class EmptyFormatter implements ArgumentFormatter<String> {
        @Override
        public String format( String argumentToFormat, String... formatterArguments ) {
            if( argumentToFormat == null ) {
                return "<null>";
            }

            if( argumentToFormat.equals( "" ) ) {
                return "<empty>";
            }

            return argumentToFormat;
        }
    }

    @DataProvider
    public static Object[][] formatterTestCases() {
        return new Object[][] {
                { "$", asList( "bool" ), asList( true ), new NotFormatter(), "", "" },
                { "$", asList( "bool" ), asList( false ), new NotFormatter(), "", "not" },
                { "$not", asList( "bool" ), asList( false ), new NotFormatter(), "", "not" },
                { "$", asList( "bool" ), asList( true ), null, "", "true" },
                { "$$ foo", asList( "bool" ), asList( true ), null, "", "\\$ foo true" },
                { "$", asList( "int5" ), asList( 5d ), new PrintfFormatter(), "%.2f", "5[.,]00" },
                { "$", asList( "obj" ), asList( new Object[] { null } ), new EmptyFormatter(), "", "<null>" },
                { "$", asList( "str" ), asList( "" ), new EmptyFormatter(), "", "<empty>" },
        };
    }

    @Test
    @UseDataProvider( "formatterTestCases" )
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void testFormatter( String value, List<String> parameterNames, List<? extends Object> parameterValues,
            ArgumentFormatter<?> formatter,
            String formatterArg,
            String expectedResult ) {
        List<ObjectFormatter<?>> asList = newArrayList();
        if( formatter != null ) {
            asList.add( new ArgumentFormatting( formatter, formatterArg ) );
        } else {
            for( int i = 0; i < parameterNames.size(); i++ ) {
                asList.add( null );
            }
        }
        List<NamedArgument> namedArguments = newArrayList();
        for( int i = 0; i < parameterNames.size(); i++ ) {
            namedArguments.add( new NamedArgument( parameterNames.get( i ), parameterValues.get( i ) ) );
        }

        List<Word> formattedWords = new StepFormatter( value, namedArguments, asList )
                .buildFormattedWords();
        List<String> formattedValues = toFormattedValues( formattedWords );
        String actualResult = Joiner.on( ' ' ).join( formattedValues );
        assertThat( actualResult ).matches( expectedResult );
    }

    private List<String> toFormattedValues( List<Word> formattedWords ) {
        List<String> result = newArrayList();
        for( Word w : formattedWords ) {
            result.add( w.getFormattedValue() );
        }
        return result;
    }

    @DataProvider
    public static Object[][] namedArgumentTestCases() {
        List<String> nameList = Arrays.asList( "int", "str", "bool" );
        List<? extends Object> valueList = Arrays.asList( 1, "some string", true );
        return new Object[][] {
                {
                        "$int $str $bool", nameList, valueList, "1 some string true"
                },
                {
                        "$str $int $bool", nameList, valueList, "some string 1 true"
                },
                {
                        "$3 $2 $int", nameList, valueList, "true some string 1"
                },
                {
                        "$bool $ $", nameList, valueList, "true 1 some string"
                },
                {
                        "$bool $2 $ $ $3", nameList, valueList, "true some string 1 some string true"
                }
        };
    }

    @Test
    @UseDataProvider( "namedArgumentTestCases" )
    public void namedArgumentTest( String value, List<String> parameterNames, List<? extends Object> parameterValues,
            String expectedValue ) {
        testFormatter( value, parameterNames, parameterValues, null, null, expectedValue );
    }
}
