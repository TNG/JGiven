package com.tngtech.jgiven.format;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.NotFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;
import com.tngtech.jgiven.format.StepFormatter;
import com.tngtech.jgiven.format.StepFormatter.Formatting;
import com.tngtech.jgiven.report.model.Word;

@RunWith( JUnitParamsRunner.class )
public class StepFormatterTest {

    public Object[] testCases() {
        return new Object[][] {
            { "a", asList(), "a" },
            { "a b", asList(), "a b" },
            { "", asList( "a" ), " a" },
            { "foo", asList( "a" ), "foo a" },
            { "", asList( "a", "b" ), " a b" },
            { "$", asList( "a" ), "a" },
            { "foo $", asList( "a" ), "foo a" },
            { "$ foo", asList( "a" ), "a foo" },
            { "foo $ foo", asList( "a" ), "foo a foo" },
            { "$ $", asList( "a", "b" ), "a b" },
            { "$d foo", asList( 5 ), "5 foo" },
            { "$1 foo $1", asList( "a" ), "a foo a" },
            { "$2 $1", asList( "a", "b" ), "b a" },
        };
    }

    @Test
    @Parameters( method = "testCases" )
    public void formatter_should_handle_dollars_correctly( String source, List<Object> arguments, String expectedResult ) {
        testFormatter( source, arguments, null, null, expectedResult );
    }

    public Object[] formatterTestCases() {
        return new Object[][] {
            { "$", asList( true ), new NotFormatter(), "", "" },
            { "$", asList( false ), new NotFormatter(), "", "not" },
            { "$not$", asList( false ), new NotFormatter(), "", "not" },
            { "$or should not$", asList( false ), new NotFormatter(), "", "not" },
            { "$or not$", asList( false ), new NotFormatter(), "", "not" },
            { "$", asList( true ), null, "", "true" },
            { "$", asList( 5d ), new PrintfFormatter(), "%.2f", "5.00" },
        };
    }

    @Test
    @Parameters( method = "formatterTestCases" )
    public void testFormatter( String source, List<Object> arguments, ArgumentFormatter<?> formatter, String formatterArg,
            String expectedResult ) {
        List<Formatting<?>> asList = new ArrayList();
        if( formatter != null ) {
            asList.add( new Formatting( formatter, formatterArg ) );
        } else {
            for( int i = 0; i < arguments.size(); i++ ) {
                asList.add( null );
            }
        }
        List<Word> formattedWords = new StepFormatter( source, arguments, asList )
            .buildFormattedWords();
        String actualResult = Joiner.on( ' ' ).join( formattedWords );
        assertThat( actualResult ).isEqualTo( expectedResult );
    }

}
