package com.tngtech.jgiven.report.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.DataTables;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.format.table.DefaultTableFormatter;
import com.tngtech.jgiven.format.table.RowFormatter;
import com.tngtech.jgiven.format.table.RowFormatterFactory;

@RunWith( DataProviderRunner.class )
public class DataTableFormatterTest {

    private static final Object[][] TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS =
            { { "h1", "h2" }, { "a1", "a2" }, { "b1", "b2" } };

    static class TestPojo {
        int x = 5;
        int y = 6;

        @Override
        public String toString() {
            return "TestPojo: " + x + ", " + y;
        }
    }

    static class AnotherPojo {
        String fieldA = "test";
        String fieldB = "testB";
    }

    @Test
    public void testToTableValue() {
        // has neither rows nor columns
        assertThat( toTableValue( new Object[][] { }, new TableAnnotation() ).getData() ).isEmpty();

        // no columns
        assertThat( toTableValue( new Object[][] { { } }, new TableAnnotation() ).getData() ).hasSize( 1 );

        try {
            // rows with non-collection type
            toTableValue( new Object[] { new Object[] { }, 5 }, new TableAnnotation() );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {
        }

        try {
            // not the same column number in all rows
            toTableValue( new Object[][] { { 1, 2 }, { 1 } }, new TableAnnotation() );
            assertThat( false ).as( "Exception should have been thrown" ).isTrue();
        } catch( JGivenWrongUsageException e ) {
        }

        // single POJO
        assertThat( toTableValue( new TestPojo(), new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "x", "y" ), Arrays.asList( "5", "6" ) );

        // single POJO without null values
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.includeNullColumns = true;
        assertThat( toTableValue( new AnotherPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "fieldA", "fieldB" ), Arrays.asList( "test", "testB" ) );

        // single POJO with null values
        AnotherPojo withNull = new AnotherPojo();
        withNull.fieldB = null;
        assertThat( toTableValue( withNull, new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "fieldA" ), Arrays.asList( "test" ) );

        // single POJO with exclusion filter
        tableAnnotation = new TableAnnotation();
        tableAnnotation.excludeFields = new String[] { "fieldB" };
        assertThat( toTableValue( new AnotherPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "fieldA" ), Arrays.asList( "test" ) );

        // single POJO with inclusion filter
        tableAnnotation = new TableAnnotation();
        tableAnnotation.includeFields = new String[] { "fieldA" };
        assertThat( toTableValue( new AnotherPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "fieldA" ), Arrays.asList( "test" ) );

        // single POJO transposed
        tableAnnotation = new TableAnnotation();
        tableAnnotation.transpose = true;
        assertThat( toTableValue( new TestPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "x", "5" ), Arrays.asList( "y", "6" ) );

        // single POJO vertical header
        tableAnnotation = new TableAnnotation();
        tableAnnotation.header = Table.HeaderType.VERTICAL;
        assertThat( toTableValue( new TestPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "x", "5" ), Arrays.asList( "y", "6" ) );

        // single POJO columnTitles set
        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        assertThat( toTableValue( new TestPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "t1", "t2" ), Arrays.asList( "5", "6" ) );

        // string array
        assertThat( toTableValue( new String[][] { { "1" } }, new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "1" ) );

        // mixed array
        assertThat( toTableValue( new Object[][] { { "a" }, { 3 } }, new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "a" ), Arrays.asList( "3" ) );

        // 2 columns
        assertThat( toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        // DataTable
        assertThat( toTableValue( DataTables.table( 2, 1, 2, 3, 4 ), new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "1", "2" ), Arrays.asList( "3", "4" ) );

        ArrayList arrayList = new ArrayList();
        arrayList.add( newArrayList( 5 ) );
        assertThat( toTableValue( arrayList, new TableAnnotation() ).getData() )
                .containsExactly( Arrays.asList( "5" ) );

        assertThat( toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, new TableAnnotation() ).getData() )
                .isEqualTo( newArrayList( newArrayList( "1", "2" ), newArrayList( "3", "4" ) ) );

        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        assertThat( toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, tableAnnotation ).getData() )
                .isEqualTo( newArrayList( newArrayList( "t1", "t2" ), newArrayList( "1", "2" ), newArrayList( "3", "4" ) ) );

        tableAnnotation = new TableAnnotation();
        tableAnnotation.columnTitles = new String[] { "t1", "t2" };
        tableAnnotation.transpose = true;
        assertThat( toTableValue( new Object[][] { { 1, 2 }, { 3, 4 } }, tableAnnotation ).getData() )
                .isEqualTo( newArrayList( newArrayList( "t1", "1", "3" ), newArrayList( "t2", "2", "4" ) ) );

    }

    public static DataTable toTableValue( Object tableValue, Table tableAnnotation ) {
        return new DefaultTableFormatter( new FormatterConfiguration() {
            @Override
            public Formatter<?> getFormatter( Class<?> typeToBeFormatted ) {
                return DefaultFormatter.INSTANCE;
            }
        }, DefaultFormatter.INSTANCE ).format( tableValue, tableAnnotation, "param1" );
    }

    @Test
    public void testNumberedRows() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedRows = true;

        assertThat( toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation ).getData() )
                .isEqualTo(
                        newArrayList( newArrayList( "#", "h1", "h2" ), newArrayList( "1", "a1", "a2" ), newArrayList( "2", "b1", "b2" ) ) );
    }

    @Test
    @DataProvider( { "", "#", "Customer Header" } )
    public void testNumberedRowsHeader( String header ) {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedRowsHeader = header;

        assertThat( toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation ).getData() )
                .isEqualTo(
                        newArrayList( newArrayList( header, "h1", "h2" ), newArrayList( "1", "a1", "a2" ),
                                newArrayList( "2", "b1", "b2" ) ) );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void testExceptionWhenNumberedRowsHeaderIsUsedWithoutHeader() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedRowsHeader = "#";
        tableAnnotation.header = Table.HeaderType.NONE;
        toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation );
    }

    @Test
    public void testNumberedColumns() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedColumns = true;
        tableAnnotation.transpose = true;
        tableAnnotation.header = Table.HeaderType.VERTICAL;

        assertThat( toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation ).getData() )
                .isEqualTo(
                        newArrayList( newArrayList( "#", "1", "2" ), newArrayList( "h1", "a1", "b1" ), newArrayList( "h2", "a2", "b2" ) ) );
    }

    @Test
    @DataProvider( { "", "#", "Customer Header" } )
    public void testNumberedColumnsHeader( String header ) {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedColumnsHeader = header;
        tableAnnotation.header = Table.HeaderType.VERTICAL;
        tableAnnotation.transpose = true;

        assertThat( toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation ).getData() )
                .isEqualTo(
                        newArrayList( newArrayList( header, "1", "2" ), newArrayList( "h1", "a1", "b1" ),
                                newArrayList( "h2", "a2", "b2" ) ) );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void testExceptionWhenNumberedColumnsHeaderIsUsedWithoutHeader() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.numberedColumnsHeader = "#";
        toTableValue( TABLE_WITH_THREE_ROWS_AND_TWO_COLUMNS, tableAnnotation );
    }

    @Test
    public void testObjectFormattingOption() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.objectFormatting = Table.ObjectFormatting.PLAIN;
        assertThat( toTableValue( new TestPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "param1" ), Arrays.asList( "TestPojo: 5, 6" ) );
    }

    @Test
    public void testCustomRowFormattingOption() {
        TableAnnotation tableAnnotation = new TableAnnotation();
        tableAnnotation.rowFormatter = TestRowFormatterFactory.class;
        assertThat( toTableValue( new TestPojo(), tableAnnotation ).getData() )
                .containsExactly( Arrays.asList( "FooBar" ), Arrays.asList( "TestPojo: 5, 6 fooBar" ) );
    }

    public static class TestRowFormatter extends RowFormatter {

        @Override
        public List<String> header() {
            return Arrays.asList( "FooBar" );
        }

        @Override
        public List<String> formatRow( Object object ) {
            return Arrays.asList( object.toString() + " fooBar" );
        }
    }

    public static class TestRowFormatterFactory implements RowFormatterFactory {

        @Override
        public RowFormatter create( Class<?> parameterType, String parameterName, Table tableAnnotation,
                Annotation[] annotations,
                FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter ) {
            return new TestRowFormatter();
        }
    }

}
