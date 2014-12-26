package com.tngtech.jgiven.format;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.DataTables;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.StepFormatter;
import com.tngtech.jgiven.report.model.StepFormatter.Formatting;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class StepFormatterTest {

    @DataProvider
    public static Object[][] testCases() {
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
    @UseDataProvider( "testCases" )
    public void formatter_should_handle_dollars_correctly( String source, List<Object> arguments, String expectedResult ) {
        testFormatter( source, arguments, null, null, expectedResult );
    }

    @DataProvider
    public static Object[][] formatterTestCases() {
        return new Object[][] {
            { "$", asList( true ), new NotFormatter(), "", "" },
            { "$", asList( false ), new NotFormatter(), "", "not" },
            { "$not$", asList( false ), new NotFormatter(), "", "not" },
            { "$or should not$", asList( false ), new NotFormatter(), "", "not" },
            { "$or not$", asList( false ), new NotFormatter(), "", "not" },
            { "$", asList( true ), null, "", "true" },
            { "$", asList( 5d ), new PrintfFormatter(), "%.2f", "5[.,]00" },
        };
    }

    @Test
    @UseDataProvider( "formatterTestCases" )
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public void testFormatter( String source, List<Object> arguments, ArgumentFormatter<?> formatter, String formatterArg,
            String expectedResult ) {
        List<Formatting<?>> asList = newArrayList();
        if( formatter != null ) {
            asList.add( new Formatting( formatter, formatterArg ) );
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

    @Test
    public void testToTableValue() {
        // has neither rows nor columns
        assertThat( StepFormatter.toTableValue( new Object[][] {} ) ).isEmpty();

        // no columns
        assertThat( StepFormatter.toTableValue( new Object[][] { {} } ) ).hasSize( 1 );

        try {
            // rows with non-collection type
            StepFormatter.toTableValue( new Object[] { new Object[] {}, 5 } );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {}

        try {
            // not the same column number in all rows
            StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 1 } } );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {}

        // string array
        assertThat( StepFormatter.toTableValue( new String[][] { { "1" } } ) )
            .containsExactly( Arrays.asList( "1" ) );

        // mixed array
        assertThat( StepFormatter.toTableValue( new Object[][] { { "a" }, { 3 } } ) )
            .containsExactly( Arrays.asList( "a" ), Arrays.asList( "3" ) );

        // 2 columns
        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } } ) )
            .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        // DataTable
        assertThat( StepFormatter.toTableValue( DataTables.table( 2, 1, 2, 3, 4 ) ) )
            .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        ArrayList arrayList = new ArrayList();
        arrayList.add( newArrayList( 5 ) );
        assertThat( StepFormatter.toTableValue( arrayList ) )
            .containsExactly(Arrays.asList("5"));

        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } } ) )
            .isEqualTo( Lists.newArrayList(
                Lists.newArrayList( "1", "2" ),
                Lists.newArrayList( "3", "4" ) )
            );
    }
}
