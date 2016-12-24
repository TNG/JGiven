package com.tngtech.jgiven.report.text;

import static net.java.quickcheck.generator.CombinedGenerators.lists;
import static net.java.quickcheck.generator.CombinedGenerators.pairs;
import static net.java.quickcheck.generator.PrimitiveGenerators.strings;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.DataProviders;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.model.DataTable;

import net.java.quickcheck.collection.Pair;
import net.java.quickcheck.junit.SeedInfo;

@RunWith( DataProviderRunner.class )
public class PlainTextTableWriterTest {

    @Rule
    public SeedInfo seedInfo = new SeedInfo();

    @Test
    public void handleNewLinesWithoutNewLines() throws Exception {
        String[][] testData = new String[][] {
            { "a", "b" },
            { "c", "d" } };
        List<List<String>> result = PlainTextTableWriter.handleNewLines( toListOfList( testData ) );
        assertThat( result ).isEqualTo( toListOfList( testData ) );
    }

    @Test
    public void handleNewLinesEmpty() throws Exception {
        String[][] testData = new String[][] { {} };
        List<List<String>> result = PlainTextTableWriter.handleNewLines( toListOfList( testData ) );
        assertThat( result ).isEqualTo( toListOfList( testData ) );
    }

    @Test
    public void handleNewLinesTwoRows() throws Exception {
        String[][] testData = new String[][] { { "a" }, { "b" } };
        List<List<String>> result = PlainTextTableWriter.handleNewLines( toListOfList( testData ) );
        assertThat( result ).isEqualTo( toListOfList( testData ) );
    }

    @Test
    @DataProvider( { "a\nx", "a\r\nx" } )
    public void handleNewLinesOneNewLine( String testString ) throws Exception {
        String[][] testData = new String[][] { { testString, "b" } };
        List<List<String>> result = PlainTextTableWriter.handleNewLines( toListOfList( testData ) );
        List<List<String>> expected = toListOfList( new String[][] { { "a", "b" }, { "x", "" } } );
        assertThat( result ).isEqualTo( expected );
    }

    List<List<String>> toListOfList( String[][] array ) {
        List<List<String>> result = Lists.newArrayList();

        for( int iRow = 0; iRow < array.length; iRow++ ) {
            List<String> row = Lists.newArrayList();
            for( int iCol = 0; iCol < array[iRow].length; iCol++ ) {
                row.add( array[iRow][iCol] );
            }
            result.add( row );
        }

        return result;

    }

    @DataProvider
    public static Object[][] randomPrintableStrings() {
        List<Pair<String, String>> values = lists( pairs( strings(), strings() ), 100 ).next();
        return DataProviders.testForEach( values );
    }

    @UseDataProvider( "randomPrintableStrings" )
    @Test
    public void handleArbitraryStringsWithoutNewlines( Pair<String, String> randomPair ) throws Exception {
        String firstString = randomPair.getFirst();
        String secondString = randomPair.getSecond();
        StringWriter sw = new StringWriter();
        PrintWriter printWriter = new PrintWriter( sw );
        List<List<String>> data = Lists.<List<String>>newArrayList( Lists.<String>newArrayList( firstString, secondString ) );
        DataTable dataTable = new DataTable( Table.HeaderType.NONE, data );
        new PlainTextTableWriter( printWriter, false ).writeDataTable( dataTable, "" );
        String s = sw.toString();
        String expected1 = firstString.equals( "" ) ? " " : firstString;
        String expected2 = secondString.equals( "" ) ? " " : secondString;
        assertThat( s ).isEqualTo( "| " + expected1 + " | " + expected2 + " |" + System.getProperty("line.separator") );

    }
}