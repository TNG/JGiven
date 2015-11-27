package com.tngtech.jgiven.impl.format;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.AnnotationFormat;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.impl.util.AnnotationUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.StepFormatter;

public class ParameterFormattingUtil {
    private static final StepFormatter.Formatting<?, ?> DEFAULT_FORMATTING = new StepFormatter.ArgumentFormatting<ArgumentFormatter<Object>, Object>(
        new DefaultFormatter<Object>() );

    private final AbstractJGivenConfiguration configuration;

    public ParameterFormattingUtil( AbstractJGivenConfiguration configuration ) {
        this.configuration = configuration;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public <T> StepFormatter.Formatting<?, ?> getFormatting( Class<T> parameterType, String parameterName, Annotation[] annotations ) {
        StepFormatter.Formatting<?, ?> formatting = getFormatting( annotations, Sets.<Class<?>>newHashSet(), null, parameterName );
        if( formatting == null ) {
            Formatter<T> formatter = (Formatter<T>) configuration.getFormatter( parameterType );
            if( formatter != null ) {
                formatting = new StepFormatter.TypeBasedFormatting<T>( formatter, annotations );
            } else {
                formatting = DEFAULT_FORMATTING;
            }
        }
        return formatting;
    }

    /**
     * Recursively searches for formatting annotations.
     *
     * @param visitedTypes used to prevent an endless loop
     * @param parameterName
     */
    private StepFormatter.Formatting<?, ?> getFormatting( Annotation[] annotations, Set<Class<?>> visitedTypes,
            Annotation originalAnnotation,
            String parameterName ) {
        for( Annotation annotation : annotations ) {
            try {
                if( annotation instanceof Format ) {
                    Format arg = (Format) annotation;
                    return new StepFormatter.ArgumentFormatting( ReflectionUtil.newInstance( arg.value() ), arg.args() );
                } else if( annotation instanceof Table ) {
                    Table tableAnnotation = (Table) annotation;
                    return new StepFormatter.TableFormatting( ReflectionUtil.newInstance( tableAnnotation.formatter() ), tableAnnotation,
                        parameterName, annotations );
                } else if( annotation instanceof AnnotationFormat ) {
                    AnnotationFormat arg = (AnnotationFormat) annotation;
                    return new StepFormatter.ArgumentFormatting(
                        new StepFormatter.AnnotationBasedFormatter( arg.value().newInstance(), originalAnnotation ) );
                } else {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if( !visitedTypes.contains( annotationType ) ) {
                        visitedTypes.add( annotationType );
                        StepFormatter.Formatting<?, ?> formatting = getFormatting( annotationType.getAnnotations(), visitedTypes,
                            annotation,
                            parameterName );
                        if( formatting != null ) {
                            return formatting;
                        }
                    }
                }
            } catch( Exception e ) {
                throw Throwables.propagate( e );
            }
        }
        return null;
    }

    public List<String> toStringList( List<StepFormatter.Formatting<?, ?>> formatter, List<?> arguments ) {
        List<String> result = Lists.newArrayList();
        for( int i = 0; i < arguments.size(); i++ ) {

            StepFormatter.Formatting<?, ?> formatting = DEFAULT_FORMATTING;
            if( i < formatter.size() && formatter.get( i ) != null ) {
                formatting = formatter.get( i );
            }
            result.add( formatUsingFormatterOrDefault( formatting, arguments.get( i ) ) );
        }
        return result;
    }

    private <T> String formatUsingFormatterOrDefault( StepFormatter.Formatting<?, T> formatting, Object o ) {
        return formatting.format( (T) o );
    }

    public List<StepFormatter.Formatting<?, ?>> getFormatter( Class<?>[] parameterTypes, List<String> parameterNames,
            Annotation[][] parameterAnnotations ) {
        List<StepFormatter.Formatting<?, ?>> res = Lists.newArrayList();
        ParameterFormattingUtil parameterFormattingUtil = new ParameterFormattingUtil( configuration );
        for( int i = 0; i < parameterTypes.length; i++ ) {
            Annotation[] annotations = parameterAnnotations[i];
            if( !AnnotationUtil.isHidden( annotations ) ) {
                String parameterName = i < parameterNames.size() ? parameterNames.get( i ) : "param" + i;
                res.add( parameterFormattingUtil.getFormatting( parameterTypes[i], parameterName, annotations ) );
            }
        }
        return res;
    }

}
