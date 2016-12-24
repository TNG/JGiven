package com.tngtech.jgiven.report.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Table.HeaderType;
import com.tngtech.jgiven.impl.util.AssertionUtil;

/**
 * Represents a data table argument.
 */
public class DataTable {

    /**
     * The type of the header
     */
    private HeaderType headerType;

    /**
     * The data of the table as a list of rows
     */
    private List<List<String>> data;

    public DataTable( HeaderType headerType, List<List<String>> data ) {
        this.headerType = headerType;
        this.data = copy( data );
    }

    private List<List<String>> copy( List<List<String>> data ) {
        List<List<String>> dataCopy = Lists.newArrayListWithExpectedSize( data.size() );

        for( List<String> row : data ) {
            dataCopy.add( new ArrayList<String>( row ) );
        }

        return dataCopy;
    }

    public HeaderType getHeaderType() {
        return headerType;
    }

    public void setHeaderType( HeaderType headerType ) {
        this.headerType = headerType;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData( List<List<String>> data ) {
        this.data = data;
    }

    public int getRowCount() {
        return data.size();
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o )
            return true;
        if( o == null || getClass() != o.getClass() )
            return false;

        DataTable dataTable = (DataTable) o;

        if( data != null ? !data.equals( dataTable.data ) : dataTable.data != null )
            return false;
        if( headerType != dataTable.headerType )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = headerType != null ? headerType.hashCode() : 0;
        result = 31 * result + ( data != null ? data.hashCode() : 0 );
        return result;
    }

    public boolean hasHorizontalHeader() {
        return headerType == HeaderType.HORIZONTAL || headerType == HeaderType.BOTH;
    }

    public boolean hasVerticalHeader() {
        return headerType == HeaderType.VERTICAL || headerType == HeaderType.BOTH;
    }

    public void addColumn( int i, List<String> column ) {
        AssertionUtil.assertTrue( data.size() == column.size(),
            "Column has different number of rows as expected. Is " + column.size() + ", but expected " + data.size() );

        for( int row = 0; row < column.size(); row++ ) {
            data.get( row ).add( i, column.get( row ) );
        }
    }

    public int getColumnCount() {
        return data.get( 0 ).size();
    }

    public void addRow( int i, List<String> row ) {
        AssertionUtil.assertTrue( getColumnCount() == row.size(),
            "Row has different number of columns as expected. Is " + row.size() + ", but expected " + getColumnCount() );

        data.add( i, row );
    }
}
