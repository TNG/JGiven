package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class PlainTextTableWriter extends PlainTextWriter {

    public PlainTextTableWriter(PrintWriter printWriter, boolean withColor) {
        super( printWriter, withColor );
    }

    static class ColumnSpec {
        int width;
        boolean leftAligned;
    }

    public void writeDataTable( List<List<String>> tableModel, String indent ) {
        StringBuilder formatBuilder = new StringBuilder();
        StringBuilder lineBuilder = new StringBuilder();
        List<ColumnSpec> columnWidths = getColumnSpecs( tableModel );
        for( ColumnSpec spec : columnWidths ) {
            formatBuilder.append( "| %" );
            if( spec.leftAligned ) {
                formatBuilder.append( "-" );
            }
            formatBuilder.append( spec.width + "s " );
            lineBuilder.append( "+" );
            lineBuilder.append( Strings.repeat( "-", spec.width + 2 ) );
        }
        formatBuilder.append( "|" );
        lineBuilder.append( "+" );

        String formatString = formatBuilder.toString();
        writer.println( indent + String.format(formatString, tableModel.get(0).toArray()) );
        writer.println( indent + lineBuilder );
        for( int nrow = 1; nrow < tableModel.size(); nrow++ ) {
            writer.println( indent + String.format( formatString, tableModel.get( nrow ).toArray() ) );
        }

    }

    private List<ColumnSpec> getColumnSpecs( List<List<String>> dataTableModel ) {
        ColumnSpec[] result = new ColumnSpec[dataTableModel.get( 0 ).size()];
        for( int nrow = 0; nrow < dataTableModel.size(); nrow++ ) {
            List<String> row = dataTableModel.get( nrow );
            for( int ncol = 0; ncol < row.size(); ncol++ ) {
                String value = row.get( ncol );
                int width = value.length();
                ColumnSpec spec = result[ncol];
                if( spec == null ) {
                    spec = new ColumnSpec();
                    result[ncol] = spec;
                }
                if( width > spec.width ) {
                    spec.width = width;
                }

                if( nrow > 0 && Doubles.tryParse( value ) == null ) {
                    spec.leftAligned = true;
                }
            }
        }
        return Lists.newArrayList( result );
    }

}
