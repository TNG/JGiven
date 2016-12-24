package com.tngtech.jgiven.report.model;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

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

@RunWith( DataProviderRunner.class )
public class StepFormatterTest {

    @DataProvider
    public static Object[][] testCases() {
        return new Object[][] {
            { "a", asList(), "a" },
            { "a b", asList(), "a b" },
            { " a ", asList(), " a " },
            { "", asList( "a" ), "a" },
            { "foo", asList( "a" ), "foo a" },
            { "", asList( "a", "b" ), "a b" },
            { "$", asList( "a" ), "a" },
            { "$foo", asList( "a" ), "a" },
            { "foo $", asList( "a" ), "foo a" },
            { "$ foo", asList( "a" ), "a foo" },
            { "foo $ foo", asList( "a" ), "foo a foo" },
            { "$ $", asList( "a", "b" ), "a b" },
            { "$foo bar$", asList( "a", "b" ), "a bar b" },
            { "foo$bar$baz x", asList( "a", "b" ), "foo a b x" },
            { "$d foo", asList( 5 ), "5 foo" },
            { "$1 foo $1", asList( "a" ), "a foo a" },
            { "$2 $1", asList( "a", "b" ), "b a" },
            { "$]", asList( "a" ), "a ]" },
            { "foo $]", asList( "a" ), "foo a ]" },
        };
    }

    @Test
    @UseDataProvider( "testCases" )
    public void formatter_should_handle_dollars_correctly( String source, List<Object> arguments, String expectedResult ) {
        testFormatter( source, arguments, null, null, expectedResult );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void missing_arguments_lead_to_exception() {
        testFormatter( "$foo bar$", asList( "a" ), null, null, "" );
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
            { "$", asList( true ), new NotFormatter(), "", "" },
            { "$", asList( false ), new NotFormatter(), "", "not" },
            { "$not", asList( false ), new NotFormatter(), "", "not" },
            { "$", asList( true ), null, "", "true" },
            { "$$ foo", asList( true ), null, "", "\\$ foo true" },
            { "$", asList( 5d ), new PrintfFormatter(), "%.2f", "5[.,]00" },
            { "$", asList( new Object[] { null } ), new EmptyFormatter(), "", "<null>" },
            { "$", asList( "" ), new EmptyFormatter(), "", "<empty>" },
        };
    }

    @Test
    @UseDataProvider( "formatterTestCases" )
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void testFormatter( String source, List<? extends Object> arguments, ArgumentFormatter<?> formatter, String formatterArg,
            String expectedResult ) {
        List<ObjectFormatter<?>> asList = newArrayList();
        if( formatter != null ) {
            asList.add( new ArgumentFormatting( formatter, formatterArg ) );
        } else {
            for( int i = 0; i < arguments.size(); i++ ) {
                asList.add( null );
            }
        }
        List<NamedArgument> namedArguments = newArrayList();
        for( Object o : arguments ) {
            namedArguments.add( new NamedArgument( "foo", o ) );
        }

        List<Word> formattedWords = new StepFormatter( source, namedArguments, asList )
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

}
