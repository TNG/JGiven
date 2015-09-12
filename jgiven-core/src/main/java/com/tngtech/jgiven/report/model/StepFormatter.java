package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.AnnotationArgumentFormatter;
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

    public static class AnnotationBasedFormatter implements ArgumentFormatter {

        private final AnnotationArgumentFormatter formatter;
        private final Annotation annotation;

        public AnnotationBasedFormatter( AnnotationArgumentFormatter formatter, Annotation annotation ) {
            this.formatter = formatter;
            this.annotation = annotation;
        }

        @Override
        public String format( Object argumentToFormat, String... formatterArguments ) {
            return formatter.format( argumentToFormat, annotation );
        }
    }

    public StepFormatter( String stepDescription, List<NamedArgument> arguments, List<Formatting<?>> formatters ) {
        this.stepDescription = stepDescription;
        this.arguments = arguments;
        this.formatters = formatters;

    }

    public List<Word> buildFormattedWords() {
        try {
            return buildFormattedWordsInternal();
        } catch( JGivenWrongUsageException e ) {
            throw new JGivenWrongUsageException( e.getMessage() + ". Step definition: " + stepDescription );
        }
    }

    private List<Word> buildFormattedWordsInternal() {
        List<Word> formattedWords = Lists.newArrayList();

        int argCount = 0;
        StringBuilder currentWord = new StringBuilder();
        for( int i = 0; i < stepDescription.length(); i++ ) {
            char ch = stepDescription.charAt( i );
            if( ch == '$' ) {
                // $$ escapes a single $
                if( i + 1 < stepDescription.length()
                        && stepDescription.charAt( i + 1 ) == '$' ) {
                    currentWord.append( "$" );
                    i++;
                } else {

                    // add current word if there is one
                    if( currentWord.length() > 0 ) {

                        // remove trailing whitespace (but only one!)
                        if( currentWord.charAt( currentWord.length() - 1 ) == ' ' ) {
                            currentWord.setLength( currentWord.length() - 1 );
                        }

                        formattedWords.add( new Word( currentWord.toString() ) );
                        currentWord.setLength( 0 );
                    }

                    i = readPlaceholder( i + 1, stepDescription, currentWord );
                    addArgument( formattedWords, argCount, currentWord );
                    argCount++;
                    currentWord.setLength( 0 );
                }
            } else {
                currentWord.append( ch );
            }
        }

        if( currentWord.length() > 0 ) {
            formattedWords.add( new Word( currentWord.toString() ) );
        }

        formattedWords.addAll( getRemainingArguments( argCount ) );
        return formattedWords;
    }

    private int readPlaceholder( int start, String stepDescription, StringBuilder currentWord ) {
        int i = start;
        for( ; i < stepDescription.length(); i++ ) {
            char ch = stepDescription.charAt( i );

            if( Character.isJavaIdentifierPart( ch ) && ch != '$' ) {
                currentWord.append( ch );
            } else {
                break;
            }
        }

        if( i < stepDescription.length() && stepDescription.charAt( i ) != ' ' ) {
            return i - 1;

        }

        return i;
    }

    private List<Word> getRemainingArguments( int argCount ) {
        List<Word> remainingArguments = Lists.newArrayList();
        for( int i = argCount; i < arguments.size(); i++ ) {
            Object value = arguments.get( i ).value;
            String formattedValue = formatUsingFormatterOrNull( formatters.get( i ), value );
            if( formattedValue == null
                    && formatters.get( i ) != null
                    && ( formatters.get( i ).formatter instanceof TableFormatter ) ) {
                Table tableAnnotation = ( (TableFormatter) formatters.get( i ).formatter ).tableAnnotation;
                remainingArguments.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ),
                    toTableValue( value, tableAnnotation ) ) );
            } else {
                remainingArguments.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ), formattedValue ) );
            }
        }
        return remainingArguments;
    }

    public static DataTable toTableValue( Object tableValue, Table tableAnnotation ) {
        DataTable dataTable = toDataTable( tableValue, tableAnnotation );
        addNumberedRows( tableAnnotation, dataTable );
        addNumberedColumns( tableAnnotation, dataTable );
        return dataTable;
    }

    private static void addNumberedRows( Table tableAnnotation, DataTable dataTable ) {
        if( tableAnnotation.numberedRows() || !tableAnnotation.numberedRowsHeader().equals( "" ) ) {
            List<String> column = Lists.newArrayListWithExpectedSize( dataTable.getRowCount() );

            if( dataTable.hasHorizontalHeader() ) {
                String header = "#";
                if( !tableAnnotation.numberedRowsHeader().equals( "" ) ) {
                    header = tableAnnotation.numberedRowsHeader();
                }
                column.add( header );
            }

            int counter = 1;
            while( column.size() != dataTable.getRowCount() ) {
                column.add( "" + counter );
                counter++;
            }
            dataTable.addColumn( 0, column );
        }
    }

    private static void addNumberedColumns( Table tableAnnotation, DataTable dataTable ) {
        if( tableAnnotation.numberedColumns() || !tableAnnotation.numberedColumnsHeader().equals( "" ) ) {
            List<String> row = Lists.newArrayListWithExpectedSize( dataTable.getColumnCount() );

            if( dataTable.hasVerticalHeader() ) {
                String header = "#";
                if( !tableAnnotation.numberedColumnsHeader().equals( "" ) ) {
                    header = tableAnnotation.numberedColumnsHeader();
                }
                row.add( header );
            }

            int counter = 1;
            while( row.size() != dataTable.getColumnCount() ) {
                row.add( "" + counter );
                counter++;
            }
            dataTable.addRow( 0, row );
        }
    }

    public static DataTable toDataTable( Object tableValue, Table tableAnnotation ) {

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
                    return pojosToTableValue( rows, tableAnnotation );
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

    static DataTable pojosToTableValue( Iterable<?> objects, final Table tableAnnotation ) {
        List<List<String>> list = Lists.newArrayList();

        Object first = objects.iterator().next();
        Iterable<Field> fields = getFields( tableAnnotation, first );

        if( tableAnnotation.columnTitles().length > 0 ) {
            list.add( Arrays.asList( tableAnnotation.columnTitles() ) );
        } else {
            list.add( getFieldNames( fields ) );
        }

        boolean[] nonNullColumns = new boolean[list.get( 0 ).size()];
        for( Object o : objects ) {
            List<Object> allFieldValues = ReflectionUtil.getAllFieldValues( o, fields, "" );
            for( int i = 0; i < allFieldValues.size(); i++ ) {
                if( allFieldValues.get( i ) != null ) {
                    nonNullColumns[i] = true;
                }
            }
            list.add( toStringList( allFieldValues ) );
        }

        if( !tableAnnotation.includeNullColumns() ) {
            list = removeNullColumns( list, nonNullColumns );
        }

        list = tableAnnotation.transpose() || tableAnnotation.header().isVertical() ? transpose( list ) : list;
        return new DataTable( tableAnnotation.header(), list );
    }

    private static List<List<String>> removeNullColumns( List<List<String>> list, boolean[] nonNullColumns ) {
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

    private static Iterable<Field> getFields( Table tableAnnotation, Object first ) {
        final Set<String> includeFields = Sets.newHashSet( tableAnnotation.includeFields() );
        final Set<String> excludeFields = Sets.newHashSet( tableAnnotation.excludeFields() );
        return FluentIterable.from( ReflectionUtil.getAllNonStaticFields( first.getClass() ) )
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
            } );
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

    private static List<String> getFieldNames( Iterable<Field> fields ) {
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

    private void addArgument( List<Word> formattedWords, int argCount, CharSequence word ) {
        Pattern pattern = Pattern.compile( "(\\d+)" );
        Matcher matcher = pattern.matcher( word );
        int index = argCount;
        if( matcher.matches() ) {
            int argIndex = Integer.parseInt( matcher.group( 1 ) );
            index = argIndex - 1;
        }

        if( index >= arguments.size() ) {
            throw new JGivenWrongUsageException( "The step definition has more placeholders than arguments" );
        }

        Object value = arguments.get( index ).value;
        String defaultFormattedValue = toDefaultStringFormat( value );

        Formatting<?> formatter = formatters.get( index );
        if( formatter != null && formatter.formatter instanceof TableFormatter ) {
            throw new JGivenWrongUsageException(
                "Parameters annotated with @Table must be the last ones. They cannot be used for $ substitution" );
        }
        String formattedValue = formatUsingFormatterOrNull( formatter, value );
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
