package com.tngtech.jgiven.report.model;

import com.google.common.base.Joiner;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.NotFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.report.model.StepFormatter.ArgumentFormatting;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith( DataProviderRunner.class )
public class StepFormatterTest {

    @DataProvider
    public static Object[][] testCases() {
        // @formatter:off
        return new Object[][] {
                { "a",             List.of(),                    List.of(),                   "a" },
                { "a b",           List.of(),                    List.of(),                   "a b" },
                { " a ",           List.of(),                    List.of(),                   " a " },
                { "",              List.of( "strA" ),        List.of( "a" ),           "a" },
                { "foo",           List.of( "strA" ),        List.of( "a" ),           "foo a" },
                { "",              List.of( "strA", "strB" ),    List.of( "a", "b" ),         "a b" },
                { "$",             List.of( "strA" ),         List.of( "a" ),          "a" },
                { "$foo",          List.of( "strA" ),         List.of( "a" ),          "a" },
                { "foo $",         List.of( "strA" ),         List.of( "a" ),          "foo a" },
                { "$ foo",         List.of( "strA" ),         List.of( "a" ),          "a foo" },
                { "foo $ foo",     List.of( "strA" ),         List.of( "a" ),          "foo a foo" },
                { "$ $",           List.of( "strA", "strB" ),     List.of( "a", "b" ),        "a b" },
                { "$foo bar$",     List.of( "strA", "strB" ),     List.of( "a", "b" ),        "a bar b" },
                { "foo$bar$baz x", List.of( "strA", "strB" ) ,    List.of( "a", "b" ),        "foo a b x" },
                { "$d foo",        List.of( "int5" ),         List.of( 5 ),            "5 foo" },
                { "$1 foo $1",     List.of( "strA" ),         List.of( "a" ),          "a foo a" },
                { "$2 $1",         List.of( "strA", "strB" ),     List.of("a", "b"),          "b a" },
                { "$]",            List.of( "strA" ),         List.of( "a" ),          "a ]" },
                { "$ $unknown",    List.of("strA" ),          List.of("a"),            "a \\$unknown" },
                { "foo $]",        List.of( "strA" ),         List.of( "a" ),          "foo a ]" },
                { "foo $$b",       List.of( "strA" ),         List.of( "a" ),          "foo \\$b a" },
                { "foo b $$ c",    List.of( "strA" ),         List.of( "a" ),          "foo b \\$ c a" }
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
                { "$", List.of( "bool" ), List.of( true ), new NotFormatter(), "", "" },
                { "$", List.of( "bool" ), List.of( false ), new NotFormatter(), "", "not" },
                { "$not", List.of( "bool" ), List.of( false ), new NotFormatter(), "", "not" },
                { "$", List.of( "bool" ), List.of( true ), null, "", "true" },
                { "$$ foo", List.of( "bool" ), List.of( true ), null, "", "\\$ foo true" },
                { "$", List.of( "int5" ), List.of( 5d ), new PrintfFormatter(), "%.2f", "5[.,]00" },
                { "$", List.of( "obj" ), Collections.singletonList( null ), new EmptyFormatter(), "", "<null>" },
                { "$", List.of( "str" ), List.of( "" ), new EmptyFormatter(), "", "<empty>" },
        };
    }

    @Test
    @UseDataProvider( "formatterTestCases" )
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void testFormatter( String value, List<String> parameterNames, List<?> parameterValues,
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
        List<String> nameList = List.of( "int", "str", "bool" );
        List<?> valueList = List.of( 1, "some string", true );
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
    public void namedArgumentTest( String value, List<String> parameterNames, List<?> parameterValues,
            String expectedValue ) {
        testFormatter( value, parameterNames, parameterValues, null, null, expectedValue );
    }
}
