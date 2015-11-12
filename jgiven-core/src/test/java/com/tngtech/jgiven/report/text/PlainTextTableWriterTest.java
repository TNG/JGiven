package com.tngtech.jgiven.report.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class PlainTextTableWriterTest {

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
    public void handleNewLinesOneNewLine() throws Exception {
        String[][] testData = new String[][] { { "a\nx", "b" } };
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
}