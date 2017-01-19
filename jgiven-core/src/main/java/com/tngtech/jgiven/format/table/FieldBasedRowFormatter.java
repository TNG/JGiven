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
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.annotation.TableFieldFormat;
import com.tngtech.jgiven.annotation.TableFieldsFormats;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

/**
 * Default implementation of the {@link RowFormatter} that uses
 * the fields of an object as columns of the table
 *
 * @See {@link com.tngtech.jgiven.annotation.Table} for details<br>
 * {@link TableFieldsFormats} annotation<br>
 */
public class FieldBasedRowFormatter extends RowFormatter {

    private final Class<?> type;
    private final Table tableAnnotation;
    private final String parameterName;
    private final Annotation[] annotations;
    private List<Field> fields;
    boolean[] nonNullColumns;
    private Map<String, Format> formatsByFieldName;

    public FieldBasedRowFormatter( Class<?> type, String parameterName, Table tableAnnotation,
            Annotation[] annotations ) {
        this.type = type;
        this.tableAnnotation = tableAnnotation;
        this.parameterName = parameterName;
        this.annotations = annotations;
        this.fields = getFields( tableAnnotation, type );
        this.nonNullColumns = new boolean[fields.size()];
        this.formatsByFieldName = retrieveFieldsFormats();
    }

    @Override
    public List<String> header() {
        return getFieldNames( fields );
    }

    @Override
    public List<String> formatRow(Object object) {
        ArgumentFormatter formatter;

        List<Object> allFieldValues = ReflectionUtil.getAllFieldValues(object,
                fields, "");

        List<String> res = Lists.newArrayList();

        for (int i = 0; i < allFieldValues.size(); i++) {
            Object v = allFieldValues.get(i);
            Field field = fields.get(i);
            Format format = formatsByFieldName.get(field.getName());

            if (v != null) {
                nonNullColumns[i] = true;
            }

            if (format == null) {
            	formatter = DefaultFormatter.INSTANCE;
                res.add(formatter.format(v));
            } else {
                Class<? extends ArgumentFormatter<?>> clazz = format.value();
                formatter = ReflectionUtil.newInstance(clazz);
                res.add(formatter.format(v, format.args()));
            }
        }

        return res;
    }

    private Map<String, Format> retrieveFieldsFormats() {
        Map<String, Format> inter = Maps.newHashMap();
        for (Annotation annotation : annotations) {
            TableFieldsFormats tffs = null;

            Class<? extends Annotation> annotationType = annotation
                    .annotationType();
            if (annotationType.isAssignableFrom(TableFieldsFormats.class)) {
                tffs = (TableFieldsFormats) annotation;
            } else if (annotationType
                    .isAnnotationPresent(TableFieldsFormats.class)) {
                tffs = (TableFieldsFormats) annotationType
                        .getAnnotation(TableFieldsFormats.class);
            }

            if (tffs != null) {
                TableFieldFormat[] tffTab = tffs.value();
                for (TableFieldFormat tff : tffTab) {
                    String fieldName = tff.value();
                    Format format = tff.format();
                    inter.put(fieldName, format);
                }
                break;
            }
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
