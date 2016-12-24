package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.util.AnnotationUtil;
import com.tngtech.jgiven.impl.util.ApiUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.DataTable;

/**
 * The default implementation to format a table argument
 */
public class DefaultTableFormatter implements TableFormatter {
    public static final String DEFAULT_NUMBERED_HEADER = "#";
    private final FormatterConfiguration formatterConfiguration;
    private final ObjectFormatter<?> objectFormatter;

    public DefaultTableFormatter( FormatterConfiguration formatterConfiguration, ObjectFormatter<?> objectFormatter ) {
        this.formatterConfiguration = formatterConfiguration;
        this.objectFormatter = objectFormatter;
    }

    @Override
    public DataTable format( Object tableArgument, Table tableAnnotation, String parameterName, Annotation... allAnnotations ) {
        DataTable dataTable = toDataTable( tableArgument, tableAnnotation, parameterName, allAnnotations );
        addNumberedRows( tableAnnotation, dataTable );
        addNumberedColumns( tableAnnotation, dataTable );
        return dataTable;
    }

    private static void addNumberedRows( Table tableAnnotation, DataTable dataTable ) {
        String customHeader = tableAnnotation.numberedRowsHeader();
        boolean hasCustomerHeader = !customHeader.equals( AnnotationUtil.ABSENT );

        if( tableAnnotation.numberedRows() || hasCustomerHeader ) {
            ApiUtil.isTrue( !hasCustomerHeader || dataTable.hasHorizontalHeader(),
                "Using numberedRowsHeader in @Table without having a horizontal header." );

            int rowCount = dataTable.getRowCount();
            List<String> column = Lists.newArrayListWithExpectedSize( rowCount );
            addHeader( customHeader, column, dataTable.hasHorizontalHeader() );
            addNumbers( rowCount, column );
            dataTable.addColumn( 0, column );
        }
    }

    private static void addNumberedColumns( Table tableAnnotation, DataTable dataTable ) {
        String customHeader = tableAnnotation.numberedColumnsHeader();
        boolean hasCustomerHeader = !customHeader.equals( AnnotationUtil.ABSENT );

        if( tableAnnotation.numberedColumns() || hasCustomerHeader ) {
            ApiUtil.isTrue( !hasCustomerHeader || dataTable.hasVerticalHeader(),
                "Using numberedColumnsHeader in @Table without having a vertical header." );

            int columnCount = dataTable.getColumnCount();
            List<String> row = Lists.newArrayListWithExpectedSize( columnCount );
            addHeader( customHeader, row, dataTable.hasVerticalHeader() );
            addNumbers( columnCount, row );
            dataTable.addRow( 0, row );
        }
    }

    private static void addHeader( String customHeader, List<String> column, boolean hasHeader ) {
        boolean hasCustomerHeader = !customHeader.equals( AnnotationUtil.ABSENT );

        if( hasHeader ) {
            String header = DEFAULT_NUMBERED_HEADER;
            if( hasCustomerHeader ) {
                header = customHeader;
            }
            column.add( header );
        }
    }

    private static void addNumbers( int count, List<String> column ) {
        int counter = 1;
        while( column.size() < count ) {
            column.add( Integer.toString( counter ) );
            counter++;
        }
    }

    private DataTable toDataTable( Object tableValue, Table tableAnnotation, String parameterName, Annotation[] annotations ) {

        List<List<String>> result = Lists.newArrayList();

        Iterable<?> rows = toIterable( tableValue );
        if( rows == null ) {
            rows = ImmutableList.of( tableValue );
        }

        boolean first = true;
        int ncols = 0;
        for( Object row : rows ) {
            if( first ) {
                if( toIterable( row ) == null ) {
                    return pojosToTableValue( rows, tableAnnotation, parameterName, annotations );
                }
            }
            List<String> values = toStringList( row );
            if( !first && ncols != values.size() ) {
                throw new JGivenWrongUsageException( "Number of columns in @Table annotated parameter is not equal for all rows. Expected "
                        + ncols + " got " + values.size() );
            }
            ncols = values.size();
            result.add( values );
            first = false;
        }

        if( tableAnnotation.columnTitles().length > 0 ) {
            result.add( 0, Arrays.asList( tableAnnotation.columnTitles() ) );
        }

        result = tableAnnotation.transpose() ? transpose( result ) : result;
        return new DataTable( tableAnnotation.header(), result );
    }

    DataTable pojosToTableValue( Iterable<?> objects, final Table tableAnnotation, String parameterName, Annotation[] annotations ) {
        Object first = objects.iterator().next();

        RowFormatterFactory objectRowFormatterFactory = ReflectionUtil.newInstance( tableAnnotation.rowFormatter() );
        RowFormatter formatter = objectRowFormatterFactory.create( first.getClass(), parameterName, tableAnnotation, annotations,
            formatterConfiguration, objectFormatter );

        List<List<String>> list = Lists.newArrayList();

        if( tableAnnotation.header() != Table.HeaderType.NONE ) {
            if( tableAnnotation.columnTitles().length > 0 ) {
                list.add( Arrays.asList( tableAnnotation.columnTitles() ) );
            } else {
                list.add( formatter.header() );
            }
        }

        for( Object o : objects ) {
            list.add( formatter.formatRow( o ) );
        }

        list = formatter.postProcess( list );

        list = tableAnnotation.transpose() || tableAnnotation.header().isVertical() ? transpose( list ) : list;
        return new DataTable( tableAnnotation.header(), list );
    }

    static List<List<String>> transpose( List<List<String>> list ) {
        List<List<String>> transposed = Lists.newArrayList();

        for( int rowIdx = 0; rowIdx < list.size(); rowIdx++ ) {
            List<String> row = list.get( rowIdx );
            for( int colIdx = 0; colIdx < row.size(); colIdx++ ) {
                if( rowIdx == 0 ) {
                    transposed.add( Lists.<String>newArrayList() );
                }
                transposed.get( colIdx ).add( row.get( colIdx ) );
            }
        }

        return transposed;
    }

    private static List<String> toStringList( Object row ) {
        List<String> list = Lists.newArrayList();

        Iterable<?> objects = toIterable( row );
        if( objects == null ) {
            throw new JGivenWrongUsageException( "@Table annotated argument cannot be converted to a data table." );
        }
        for( Object o : objects ) {
            list.add( toDefaultStringFormat( o ) );
        }

        return list;
    }

    private static Iterable<?> toIterable( Object value ) {
        if( value instanceof Iterable<?> ) {
            return (Iterable<?>) value;
        }
        if( value.getClass().isArray() ) {
            return arrayToList( value );
        }
        return null;
    }

    private static Iterable<?> arrayToList( Object array ) {
        int length = Array.getLength( array );
        if( length == 0 ) {
            return Collections.emptyList();
        }
        List<Object> result = Lists.newArrayList();
        for( int i = 0; i < length; i++ ) {
            result.add( Array.get( array, i ) );
        }
        return result;
    }

    private static String toDefaultStringFormat( Object value ) {
        return DefaultFormatter.INSTANCE.format( value );
    }

    public static class Factory implements TableFormatterFactory {
        @Override
        public TableFormatter create( FormatterConfiguration formatterConfiguration, ObjectFormatter<?> objectFormatter ) {
            return new DefaultTableFormatter( formatterConfiguration, objectFormatter );
        }
    }
}
