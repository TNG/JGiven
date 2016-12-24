package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.tngtech.jgiven.report.model.DataTable;

public class PlainTextTableWriter extends PlainTextWriter {

    public PlainTextTableWriter( PrintWriter printWriter, boolean withColor ) {
        super( printWriter, withColor );
    }

    static class ColumnSpec {
        int width;
        boolean leftAligned;
    }

    public void writeDataTable( DataTable dataTable, String indent ) {
        StringBuilder formatBuilder = new StringBuilder();
        StringBuilder lineBuilder = new StringBuilder();

        List<List<String>> tableModel = handleNewLines( dataTable.getData() );

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
        writer.println( indent + String.format( formatString, tableModel.get( 0 ).toArray() ) );
        if( dataTable.getHeaderType().isHorizontal() ) {
            writer.println( indent + lineBuilder );
        }
        for( int nrow = 1; nrow < tableModel.size(); nrow++ ) {
            writer.println( indent + String.format( formatString, tableModel.get( nrow ).toArray() ) );
        }

    }

    /**
     * Handles newlines by removing them and add new rows instead 
     */
    static List<List<String>> handleNewLines( List<List<String>> tableModel ) {
        List<List<String>> result = Lists.newArrayListWithExpectedSize( tableModel.size() );

        for( List<String> row : tableModel ) {
            if( hasNewline( row ) ) {
                result.addAll( splitRow( row ) );
            } else {
                result.add( row );
            }
        }

        return result;
    }

    static private Collection<List<String>> splitRow( List<String> row ) {

        List<List<String>> columns = Lists.newArrayListWithExpectedSize( row.size() );

        int nRows = 0;
        for( String cell : row ) {
            List<String> lines = FluentIterable.from( Splitter.onPattern( "\\r?\\n" ).split( cell ) ).toList();
            if( lines.size() > nRows ) {
                nRows = lines.size();
            }
            columns.add( lines );
        }

        List<List<String>> rows = Lists.newArrayListWithCapacity( nRows );

        for( int iRow = 0; iRow < nRows; iRow++ ) {
            List<String> newRow = Lists.newArrayListWithExpectedSize( row.size() );
            for( int iCol = 0; iCol < columns.size(); iCol++ ) {
                List<String> column = columns.get( iCol );
                String cell = "";
                if( iRow < column.size() ) {
                    cell = column.get( iRow );
                }
                newRow.add( cell );
            }
            rows.add( newRow );
        }

        return rows;
    }

    static private boolean hasNewline( List<String> row ) {
        for( String cell : row ) {
            if( cell.contains( "\n" ) ) {
                return true;
            }
        }
        return false;
    }

    private List<ColumnSpec> getColumnSpecs( List<List<String>> dataTableModel ) {
        ColumnSpec[] result = new ColumnSpec[dataTableModel.get( 0 ).size()];
        for( int nrow = 0; nrow < dataTableModel.size(); nrow++ ) {
            List<String> row = dataTableModel.get( nrow );
            for( int ncol = 0; ncol < row.size(); ncol++ ) {
                String value = row.get( ncol );
                int width = Math.max( value.length(), 1 );
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
