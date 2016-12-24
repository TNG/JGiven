package com.tngtech.jgiven.impl.format;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.AnnotationFormat;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.config.FormatterConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.format.table.TableFormatter;
import com.tngtech.jgiven.format.table.TableFormatterFactory;
import com.tngtech.jgiven.impl.util.AnnotationUtil;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.StepFormatter;
import com.tngtech.jgiven.report.model.StepFormatter.ChainedFormatting;
import com.tngtech.jgiven.report.model.StepFormatter.Formatting;

public class ParameterFormattingUtil {
    private static final StepFormatter.Formatting<?, ?> DEFAULT_FORMATTING = new StepFormatter.ArgumentFormatting<ArgumentFormatter<Object>, Object>(
        new DefaultFormatter<Object>() );

    private final FormatterConfiguration configuration;

    public ParameterFormattingUtil( FormatterConfiguration configuration ) {
        this.configuration = configuration;
    }

    @SuppressWarnings( { "rawtypes", "unchecked" } )
    public <T> ObjectFormatter<?> getFormatting( Class<T> parameterType, String parameterName, Annotation[] annotations ) {
        ObjectFormatter<?> formatting = getFormatting( annotations, Sets.<Class<?>>newHashSet(), null, parameterName );
        if( formatting != null ) {
            return formatting;
        }

        Formatter<T> formatter = (Formatter<T>) configuration.getFormatter( parameterType );
        if( formatter != null ) {
            return new StepFormatter.TypeBasedFormatting<T>( formatter, annotations );
        }

        return DEFAULT_FORMATTING;
    }

    /**
     * Recursively searches for formatting annotations.
     *
     * @param visitedTypes used to prevent an endless loop
     * @param parameterName
     */
    private StepFormatter.Formatting<?, ?> getFormatting( Annotation[] annotations, Set<Class<?>> visitedTypes,
            Annotation originalAnnotation, String parameterName ) {
        List<StepFormatter.Formatting<?, ?>> foundFormatting = Lists.newArrayList();
        Table tableAnnotation = null;
        for( Annotation annotation : annotations ) {
            try {
                if( annotation instanceof Format ) {
                    Format arg = (Format) annotation;
                    foundFormatting.add( new StepFormatter.ArgumentFormatting( ReflectionUtil.newInstance( arg.value() ), arg.args() ) );
                } else if( annotation instanceof Table ) {
                    tableAnnotation = (Table) annotation;
                } else if( annotation instanceof AnnotationFormat ) {
                    AnnotationFormat arg = (AnnotationFormat) annotation;
                    foundFormatting.add( new StepFormatter.ArgumentFormatting(
                        new StepFormatter.AnnotationBasedFormatter( arg.value().newInstance(), originalAnnotation ) ) );
                } else {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if( !visitedTypes.contains( annotationType ) ) {
                        visitedTypes.add( annotationType );
                        StepFormatter.Formatting<?, ?> formatting = getFormatting( annotationType.getAnnotations(), visitedTypes,
                            annotation, parameterName );
                        if( formatting != null ) {
                            foundFormatting.add( formatting );
                        }
                    }
                }
            } catch( Exception e ) {
                throw Throwables.propagate( e );
            }
        }

        if( foundFormatting.size() > 1 ) {
            Formatting<?, ?> innerFormatting = Iterables.getLast( foundFormatting );
            foundFormatting.remove( innerFormatting );

            ChainedFormatting<?> chainedFormatting = new StepFormatter.ChainedFormatting<Object>( (ObjectFormatter<Object>) innerFormatting );
            for( StepFormatter.Formatting<?, ?> formatting : Lists.reverse( foundFormatting ) ) {
                chainedFormatting.addFormatting( (StepFormatter.Formatting<?, String>) formatting );
            }

            foundFormatting.clear();
            foundFormatting.add( chainedFormatting );
        }

        if( tableAnnotation != null ) {
            ObjectFormatter<?> objectFormatter = foundFormatting.isEmpty()
                    ? DefaultFormatter.INSTANCE
                    : foundFormatting.get( 0 );
            return getTableFormatting( annotations, parameterName, tableAnnotation, objectFormatter );
        }

        if( foundFormatting.isEmpty() ) {
            return null;
        }

        return foundFormatting.get( 0 );
    }

    private StepFormatter.Formatting<?, ?> getTableFormatting( Annotation[] annotations, String parameterName, Table annotation,
            ObjectFormatter<?> objectFormatter ) {
        Table tableAnnotation = annotation;

        TableFormatterFactory factory = createTableFormatterFactory( parameterName, tableAnnotation );

        TableFormatter tableFormatter = factory.create( configuration, objectFormatter );

        return new StepFormatter.TableFormatting( tableFormatter, tableAnnotation, parameterName, annotations );
    }

    private TableFormatterFactory createTableFormatterFactory( String parameterName, Table tableAnnotation ) {
        Class<? extends TableFormatterFactory> formatterFactoryClass = tableAnnotation.formatter();

        try {
            return ReflectionUtil.newInstance( formatterFactoryClass );
        } catch( Exception e ) {
            throw new JGivenWrongUsageException(
                "Could not create an instance of " + formatterFactoryClass.getName()
                        + " which was specified at the @Table annotation for parameter '" + parameterName
                        + "'. Most likely this was due to a missing default constructor",
                TableFormatterFactory.class, e );
        }
    }

    public List<String> toStringList( List<ObjectFormatter<?>> formatter, List<?> arguments ) {
        List<String> result = Lists.newArrayList();
        for( int i = 0; i < arguments.size(); i++ ) {

            ObjectFormatter<?> formatting = DEFAULT_FORMATTING;
            if( i < formatter.size() && formatter.get( i ) != null ) {
                formatting = formatter.get( i );
            }
            result.add( formatUsingFormatterOrDefault( formatting, arguments.get( i ) ) );
        }
        return result;
    }

    private <T> String formatUsingFormatterOrDefault( ObjectFormatter<T> formatting, Object o ) {
        return formatting.format( (T) o );
    }

    public List<ObjectFormatter<?>> getFormatter( Class<?>[] parameterTypes, List<String> parameterNames,
            Annotation[][] parameterAnnotations ) {
        List<ObjectFormatter<?>> res = Lists.newArrayList();
        for( int i = 0; i < parameterTypes.length; i++ ) {
            Annotation[] annotations = parameterAnnotations[i];
            if( !AnnotationUtil.isHidden( annotations ) ) {
                String parameterName = i < parameterNames.size() ? parameterNames.get( i ) : "param" + i;
                res.add( this.getFormatting( parameterTypes[i], parameterName, annotations ) );
            }
        }
        return res;
    }

}
