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
import com.tngtech.jgiven.annotation.Table;
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
            { "$$ foo", asList( true ), null, "", "\\$ foo true" },
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

    static class TestPojo {
        int x = 5;
        int y = 6;
    }

    static class AnotherPojo {
        String fieldA = "test";
        String fieldB;
    }

    @Test
    public void testToTableValue() {
        // has neither rows nor columns
        assertThat( StepFormatter.toTableValue( new Object[][] {}, new TableAnnotation() ).getData() ).isEmpty();

        // no columns
        assertThat( StepFormatter.toTableValue( new Object[][] { {} }, new TableAnnotation() ).getData() ).hasSize( 1 );

        try {
            // rows with non-collection type
            StepFormatter.toTableValue( new Object[] { new Object[] {}, 5 }, new TableAnnotation() );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {}

        try {
            // not the same column number in all rows
            StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 1 } }, new TableAnnotation() );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {}

        // single POJO
        assertThat( StepFormatter.toTableValue( new TestPojo(), new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "x", "y" ), Arrays.asList( "5", "6" ) );

        // single POJO with null values
        assertThat( StepFormatter.toTableValue( new AnotherPojo(), new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "fieldA", "fieldB" ), Arrays.asList( "test", "null" ) );

        // single POJO with exclusion filter
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.excludeFields = new String[] { "fieldB" };
        assertThat( StepFormatter.toTableValue( new AnotherPojo(), tableAnnotation ).getData() )
            .containsExactly( Arrays.asList( "fieldA" ), Arrays.asList( "test" ) );

        // single POJO with inclusion filter
        tableAnnotation = new TableAnnotation();
        tableAnnotation.includeFields = new String[] { "fieldA" };
        assertThat( StepFormatter.toTableValue( new AnotherPojo(), tableAnnotation ).getData() )
            .containsExactly( Arrays.asList( "fieldA" ), Arrays.asList( "test" ) );

        // single POJO transposed
        tableAnnotation = new TableAnnotation();
        tableAnnotation.transpose = true;
        assertThat( StepFormatter.toTableValue( new TestPojo(), tableAnnotation ).getData() )
            .containsExactly( Arrays.asList( "x", "5" ), Arrays.asList( "y", "6" ) );

        // single POJO vertical header
        tableAnnotation = new TableAnnotation();
        tableAnnotation.header = Table.HeaderType.VERTICAL;
        assertThat( StepFormatter.toTableValue( new TestPojo(), tableAnnotation ).getData() )
            .containsExactly( Arrays.asList( "x", "5" ), Arrays.asList( "y", "6" ) );

        // single POJO columnTitles set
        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        assertThat( StepFormatter.toTableValue( new TestPojo(), tableAnnotation ).getData() )
            .containsExactly( Arrays.asList( "t1", "t2" ), Arrays.asList( "5", "6" ) );

        // string array
        assertThat( StepFormatter.toTableValue( new String[][] { { "1" } }, new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "1" ) );

        // mixed array
        assertThat( StepFormatter.toTableValue( new Object[][] { { "a" }, { 3 } }, new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "a" ), Arrays.asList( "3" ) );

        // 2 columns
        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        // DataTable
        assertThat( StepFormatter.toTableValue( DataTables.table( 2, 1, 2, 3, 4 ), new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        ArrayList arrayList = new ArrayList();
        arrayList.add( newArrayList( 5 ) );
        assertThat( StepFormatter.toTableValue( arrayList, new TableAnnotation() ).getData() )
            .containsExactly( Arrays.asList( "5" ) );

        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, new TableAnnotation() ).getData() )
            .isEqualTo( Lists.newArrayList(
                Lists.newArrayList( "1", "2" ),
                Lists.newArrayList( "3", "4" ) )
            );

        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, tableAnnotation ).getData() )
            .isEqualTo( Lists.newArrayList(
                Lists.newArrayList( "t1", "t2" ),
                Lists.newArrayList( "1", "2" ),
                Lists.newArrayList( "3", "4" ) )
            );

        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        tableAnnotation.transpose = true;
        assertThat( StepFormatter.toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, tableAnnotation ).getData() )
            .isEqualTo( Lists.newArrayList(
                Lists.newArrayList( "t1", "1", "3" ),
                Lists.newArrayList( "t2", "2", "4" ) )
            );

    }
}
