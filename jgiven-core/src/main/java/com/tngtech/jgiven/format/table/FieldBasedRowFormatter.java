package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

/**
 * Default implementation of the {@link RowFormatter} that uses
 * the fields of an object as columns of the table
 *
 * see {@link com.tngtech.jgiven.annotation.Table} for details
 */
public class FieldBasedRowFormatter extends RowFormatter {

    private final Class<?> type;
    private final Table tableAnnotation;
    private final String parameterName;
    private final Annotation[] annotations;
    private List<Field> fields;
    boolean[] nonNullColumns;

    public FieldBasedRowFormatter( Class<?> type, String parameterName, Table tableAnnotation,
            Annotation[] annotations ) {
        this.type = type;
        this.tableAnnotation = tableAnnotation;
        this.parameterName = parameterName;
        this.annotations = annotations;
        this.fields = getFields( tableAnnotation, type );
        this.nonNullColumns = new boolean[fields.size()];
    }

    @Override
    public List<String> header() {
        return getFieldNames( fields );
    }

    @Override
    public List<String> formatRow( Object object ) {
        List<Object> allFieldValues = ReflectionUtil.getAllFieldValues( object, fields, "" );
        for( int i = 0; i < allFieldValues.size(); i++ ) {
            if( allFieldValues.get( i ) != null ) {
                nonNullColumns[i] = true;
            }
        }
        return toStringList( allFieldValues );
    }

    private static List<Field> getFields( Table tableAnnotation, Class<?> type ) {
        final Set<String> includeFields = Sets.newHashSet( tableAnnotation.includeFields() );
        final Set<String> excludeFields = Sets.newHashSet( tableAnnotation.excludeFields() );
        return FluentIterable.from( ReflectionUtil.getAllNonStaticFields( type ) ).filter( new Predicate<Field>() {
            @Override
            public boolean apply( Field input ) {
                String name = input.getName();
                if( !includeFields.isEmpty() ) {
                    return includeFields.contains( name );
                }

                if( excludeFields.contains( name ) ) {
                    return false;
                }

                return true;
            }
        } ).toList();
    }

    private static List<String> getFieldNames( Iterable<Field> fields ) {
        return FluentIterable.from( ReflectionUtil.getAllFieldNames( fields ) ).transform( new Function<String, String>() {
            @Override
            public String apply( String input ) {
                return input.replace( '_', ' ' );
            }
        } ).toList();
    }

    private static List<String> toStringList( List<Object> values ) {
        List<String> list = Lists.newArrayList();

        for( Object v : values ) {
            list.add( DefaultFormatter.INSTANCE.format( v ) );
        }

        return list;
    }

    @Override
    public List<List<String>> postProcess( List<List<String>> list ) {
        if( !tableAnnotation.includeNullColumns() ) {
            return removeNullColumns( list );
        }
        return list;
    }

    private List<List<String>> removeNullColumns( List<List<String>> list ) {
        List<List<String>> newList = Lists.newArrayListWithCapacity( list.size() );
        for( List<String> row : list ) {
            List<String> newRow = Lists.newArrayList();
            newList.add( newRow );
            for( int i = 0; i < nonNullColumns.length; i++ ) {
                if( nonNullColumns[i] ) {
                    newRow.add( row.get( i ) );
                }
            }
        }
        return newList;
    }

    /**
     * Factory for creating instances of {@link com.tngtech.jgiven.format.table.FieldBasedRowFormatter}
     *
     * @see com.tngtech.jgiven.format.table.FieldBasedRowFormatter
     * @see com.tngtech.jgiven.format.table.RowFormatterFactory
     * @see com.tngtech.jgiven.annotation.Table
     * @see PlainRowFormatter.Factory
     * @since 0.9.6
     */
    public static class Factory implements RowFormatterFactory {
        @Override
        public RowFormatter create( Class<?> parameterType, String parameterName, Table tableAnnotation,
                Annotation[] annotations,
                FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter ) {
            return new FieldBasedRowFormatter( parameterType, parameterName, tableAnnotation, annotations );
        }
    }
}
