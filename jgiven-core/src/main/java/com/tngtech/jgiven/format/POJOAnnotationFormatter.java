package com.tngtech.jgiven.format;

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
import com.tngtech.jgiven.annotation.NamedFormatSet;
import com.tngtech.jgiven.annotation.POJOFormat;
import com.tngtech.jgiven.annotation.POJOFormat.BracketsEnum;
import com.tngtech.jgiven.config.DefaultConfiguration;
import com.tngtech.jgiven.impl.format.ParameterFormattingUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

/**
 * {@link com.tngtech.jgiven.format.AnnotationArgumentFormatter} that is used by the {@link POJOFormat}
 * annotation
 */
public class POJOAnnotationFormatter
        implements AnnotationArgumentFormatter<POJOFormat> {

    private ParameterFormattingUtil pfu = new ParameterFormattingUtil( new DefaultConfiguration() );

    @Override
    public String format( Object obj, POJOFormat annotation ) {
        List<Field> fields = getFields( obj.getClass(), annotation );
        boolean[] nonNullColumns = new boolean[fields.size()];

        Map<String, ObjectFormatter<?>> formattersByFieldName = retrieveFieldsFormatters( annotation, fields );

        StringBuffer sb = new StringBuffer();
        BracketsEnum brackets = annotation.brackets();
        sb.append( brackets.getOpening() );
        String sep = "";
        List<String> values = formatRow( obj, fields, formattersByFieldName, nonNullColumns );
        List<String> headers = getFieldNames( fields );
        for( int i = 0; i < values.size(); i++ ) {
            if( ( nonNullColumns[i] ) || ( ( !nonNullColumns[i] ) && annotation.includeNullColumns() ) ) {
                sb.append( sep );
                if( annotation.prefixWithFieldName() ) {
                    sb.append( headers.get( i ) );
                    sb.append( "=" );
                }
                sb.append( values.get( i ) );
                sep = annotation.fieldSeparator();
            }
        }
        sb.append( brackets.getClosing() );
        return sb.toString();
    }

    @SuppressWarnings( "unchecked" )
    private List<String> formatRow( Object object, List<Field> fields, Map<String, ObjectFormatter<?>> formattersByFieldNames,
            boolean[] nonNullColumns ) {
        List<Object> allFieldValues = ReflectionUtil.getAllFieldValues( object, fields, "" );

        List<String> res = Lists.newArrayList();

        for( int i = 0; i < allFieldValues.size(); i++ ) {
            Object v = allFieldValues.get( i );
            Field field = fields.get( i );

            if( v != null ) {
                nonNullColumns[i] = true;

                @SuppressWarnings( "rawtypes" )
                ObjectFormatter formatter = formattersByFieldNames.get( field.getName() );
                if( formatter != null ) {
                    res.add( formatter.format( v ) );
                } else {
                    formatter = DefaultFormatter.INSTANCE;
                    res.add( formatter.format( v ) );
                }
            } else {
                nonNullColumns[i] = false;

                res.add( null );
            }
        }

        return res;
    }

    private Map<String, ObjectFormatter<?>> retrieveFieldsFormatters( POJOFormat annotation, List<Field> fields ) {
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

        // Array of NamedFormat has precedence over NamedFormatSet
        nftab = annotation.fieldsFormat();
        if( nftab.length == 0 ) {
            // Fall back on a custom NamedFormatSet annotation
            Class<? extends Annotation> aclazz = annotation.fieldsFormatSetAnnotation();
            if( aclazz.isAnnotationPresent( NamedFormatSet.class ) ) {
                NamedFormatSet nfset = aclazz.getAnnotation( NamedFormatSet.class );
                nftab = nfset.value();
            }
        }

        for( NamedFormat nf : nftab ) {
            ObjectFormatter<?> formatter;

            // Custom format annotation has precedence here
            Class<? extends Annotation> cfa = nf.customFormatAnnotation();
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

    private List<Field> getFields( Class<?> type, POJOFormat annotation ) {
        final Set<String> includeFields = Sets.newHashSet( annotation.includeFields() );
        final Set<String> excludeFields = Sets.newHashSet( annotation.excludeFields() );
        return FluentIterable.from( ReflectionUtil.getAllNonStaticFields( type ) )
            .filter( new Predicate<Field>() {
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
        return FluentIterable.from( ReflectionUtil.getAllFieldNames( fields ) )
            .transform( new Function<String, String>() {
                @Override
                public String apply( String input ) {
                    return input.replace( '_', ' ' );
                }
            } ).toList();
    }

}
