package com.tngtech.jgiven.report.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.DefaultFormatter;
import com.tngtech.jgiven.format.TableFormatter;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

public class StepFormatter {
    private final String stepDescription;
    private final List<NamedArgument> arguments;
    private final List<Formatting<?>> formatters;

    public static class Formatting<T> {
        private final ArgumentFormatter<T> formatter;
        private final String[] args;

        public Formatting( ArgumentFormatter<T> formatter, String... args ) {
            this.formatter = formatter;
            this.args = args;
        }

        public String format( T o ) {
            return formatter.format( o, args );
        }
    }

    public StepFormatter( String stepDescription, List<NamedArgument> arguments, List<Formatting<?>> formatters ) {
        this.stepDescription = stepDescription;
        this.arguments = arguments;
        this.formatters = formatters;

    }

    public List<Word> buildFormattedWords() {
        List<Word> formattedWords = Lists.newArrayList();
        int argCount = 0;
        List<String> words = Splitter.on( ' ' ).splitToList( stepDescription );
        for( int i = 0; i < words.size(); i++ ) {
            String word = words.get( i );
            if( word.startsWith( "$" ) ) {
                int argEnd = findArgumentEnd( i, words );
                formatArgument( formattedWords, argCount, word );
                if( argEnd != -1 ) {
                    i = argEnd;
                }
                argCount++;
            } else {
                if( !formattedWords.isEmpty() ) {
                    Word previousWord = formattedWords.get( formattedWords.size() - 1 );
                    if( !previousWord.isArg() ) {
                        previousWord.append( word );
                    } else {
                        formattedWords.add( new Word( word ) );
                    }
                } else {
                    formattedWords.add( new Word( word ) );
                }
            }
        }
        for( int i = argCount; i < arguments.size(); i++ ) {
            Object value = arguments.get( i ).value;
            String formattedValue = formatUsingFormatterOrNull( formatters.get( i ), value );
            if( formattedValue == null
                    && formatters.get( i ) != null
                    && ( formatters.get( i ).formatter instanceof TableFormatter ) ) {
                Table tableAnnotation = ((TableFormatter)formatters.get( i ).formatter).tableAnnotation;
                formattedWords.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ),
                        toTableValue( value, tableAnnotation ) ) );
            } else {
                formattedWords.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ), formattedValue ) );
            }
        }
        return formattedWords;
    }

    public static DataTable toTableValue(Object tableValue, Table tableAnnotation) {
        List<List<String>> result = Lists.newArrayList();

        Iterable<?> rows = toIterable( tableValue );
        if (rows == null) {
            rows = ImmutableList.of(tableValue);
        }

        boolean first = true;
        int ncols = 0;
        for( Object row : rows ) {
            if( first ) {
                if( toIterable( row ) == null ) {
                    return pojosToTableValue( tableValue, tableAnnotation );
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

        result = tableAnnotation.transpose() ? transpose(result) : result;
        return new DataTable(tableAnnotation.header(), result);
    }

    private static DataTable pojosToTableValue(Object tableValue, Table tableAnnotation) {
        List<List<String>> list = Lists.newArrayList();

        Iterable<?> objects = toIterable( tableValue );
        if (objects == null) {
            objects = ImmutableList.of(tableValue);
        }
        Object first = objects.iterator().next();
        List<Field> fields = ReflectionUtil.getAllNonStaticFields( first.getClass() );
        list.add( getFieldNames( fields ) );

        for( Object o : objects ) {
            list.add( toStringList( ReflectionUtil.getAllFieldValues( o, fields, "" ) ) );
        }

        list = tableAnnotation.transpose() || tableAnnotation.header().isVertical() ? transpose(list) : list;
        return new DataTable(tableAnnotation.header(), list);
    }

    static List<List<String>> transpose(List<List<String>> list) {
        List<List<String>> transposed = Lists.newArrayList();

        for (int rowIdx = 0; rowIdx < list.size(); rowIdx++) {
            List<String> row = list.get(rowIdx);
            for (int colIdx = 0; colIdx < row.size(); colIdx++ ) {
                if (rowIdx == 0) {
                    transposed.add(Lists.<String>newArrayList());
                }
                transposed.get(colIdx).add(row.get(colIdx));
            }
        }

        return transposed;
    }

    private static List<String> getFieldNames( List<Field> fields ) {
        return FluentIterable.from( ReflectionUtil.getAllFieldNames( fields ) )
            .transform( new Function<String, String>() {
                @Override
                public String apply( String input ) {
                    return input.replace( '_', ' ' );
                }
            } ).toList();
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
            Object[] array = (Object[]) value;
            return Arrays.asList( array );
        }
        return null;
    }

    private int findArgumentEnd( int i, List<String> words ) {
        for( int j = i; j < words.size(); j++ ) {
            String word = words.get( j );
            if( word.endsWith( "$" ) && word.length() > 1 ) {
                return j;
            }
        }
        return -1;
    }

    private void formatArgument( List<Word> formattedWords, int argCount, String word ) {
        Pattern pattern = Pattern.compile( "\\$(\\d+)" );
        Matcher matcher = pattern.matcher( word );
        int index = argCount;
        if( matcher.matches() ) {
            int argIndex = Integer.parseInt( matcher.group( 1 ) );
            index = argIndex - 1;
        }

        Object value = arguments.get( index ).value;
        String defaultFormattedValue = toDefaultStringFormat( value );
        String formattedValue = formatUsingFormatterOrNull( formatters.get( index ), value );
        String argumentName = arguments.get( index ).name;

        if( defaultFormattedValue != null && !defaultFormattedValue.isEmpty() ) {
            formattedWords.add( Word.argWord( argumentName, defaultFormattedValue, formattedValue ) );
        }
    }

    @SuppressWarnings( "unchecked" )
    private <T> String formatUsingFormatterOrNull( Formatting<T> argumentFormatter, Object value ) {
        if( argumentFormatter == null ) {
            return null;
        }

        return argumentFormatter.format( (T) value );
    }

    private static String toDefaultStringFormat( Object value ) {
        return new DefaultFormatter<Object>().format( value );
    }
}
