package com.tngtech.jgiven.format.table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.NamedFormat;
import com.tngtech.jgiven.annotation.NamedFormats;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.format.ParameterFormattingUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

/**
 * Default implementation of the {@link RowFormatter} that uses the fields of an
 * object as columns of the table
 *
 * @See {@link com.tngtech.jgiven.annotation.Table} for details<br>
 *      {@link NamedFormats} annotation<br>
 */
public class FieldBasedRowFormatter extends RowFormatter {

    private ParameterFormattingUtil pfu = new ParameterFormattingUtil( new DefaultConfiguration() );
    private final Table tableAnnotation;
    private List<Field> fields;
    boolean[] nonNullColumns;
    Map<String, ObjectFormatter<?>> formattersByFieldName;

    public FieldBasedRowFormatter( Class<?> type, String parameterName, Table tableAnnotation,
            Annotation[] annotations ) {
        this.tableAnnotation = tableAnnotation;
        this.fields = getFields( tableAnnotation, type );
        this.nonNullColumns = new boolean[fields.size()];
        this.formattersByFieldName = retrieveFieldsFormatters( tableAnnotation, fields );
    }

    @Override
    public List<String> header() {
        return getFieldNames( fields );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public List<String> formatRow( Object object ) {
        List<Object> allFieldValues = ReflectionUtil.getAllFieldValues( object, fields, "" );

        List<String> res = Lists.newArrayList();

        for( int i = 0; i < allFieldValues.size(); i++ ) {
            Object v = allFieldValues.get( i );
            Field field = fields.get( i );

            if( v != null ) {
                nonNullColumns[i] = true;
            }

            @SuppressWarnings( "rawtypes" )
            ObjectFormatter formatter = formattersByFieldName.get( field.getName() );
            if( formatter != null ) {
                res.add( formatter.format( v ) );
            } else {
                formatter = DefaultFormatter.INSTANCE;
                res.add( formatter.format( v ) );
            }
        }

        return res;

    }

    private Map<String, ObjectFormatter<?>> retrieveFieldsFormatters( Table annotation, List<Field> fields ) {
        Map<String, ObjectFormatter<?>> inter = Maps.newHashMap();

        // First, look for any format defined at field level
        for( int i = 0; i < fields.size(); i++ ) {
            Field field = fields.get( i );

            ObjectFormatter<?> formatter = pfu.getFormatting( field.getType(), field.getName(), field.getAnnotations() );

            // Finally, bind format to the field when found
            if( formatter != null ) {
                inter.put( field.getName(), formatter );
            }
        }

        // Then, override with any formats specified through the Table
        // annotation
        NamedFormat[] nftab;

        // Array of NamedFormat has precedence over NamedFormats
        nftab = annotation.fieldsFormat();
        if( nftab.length == 0 ) {
            // Fall back on a custom NamedFormats annotation
            Class<? extends Annotation> aclazz = annotation.fieldsFormatSetAnnotation();
            if( aclazz.isAnnotationPresent( NamedFormats.class ) ) {
                NamedFormats nfset = aclazz.getAnnotation( NamedFormats.class );
                nftab = nfset.value();
            }
        }

        for( NamedFormat nf : nftab ) {
            ObjectFormatter<?> formatter;

            // Custom format annotation has precedence here
            Class<? extends Annotation> cfa = nf.formatAnnotation();
            if( cfa.equals( Annotation.class ) ) {
                // Custom format annotation not set, fallback on any format
                formatter = pfu.getFormatting( Object.class, nf.name(), new Annotation[] { nf.format() } );
            } else {
                formatter = pfu.getFormatting( Object.class, nf.name(), cfa.getAnnotations() );
            }

            inter.put( nf.name(), formatter );
        }

        return inter;
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
     * Factory for creating instances of
     * {@link com.tngtech.jgiven.format.table.FieldBasedRowFormatter}
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
                Annotation[] annotations, FormatterConfiguration configuration, ObjectFormatter<?> objectFormatter ) {
            return new FieldBasedRowFormatter( parameterType, parameterName, tableAnnotation, annotations );
        }
    }
}
