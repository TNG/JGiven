package com.tngtech.jgiven.report.model;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.*;
import com.tngtech.jgiven.format.table.TableFormatter;
import com.tngtech.jgiven.impl.util.WordUtil;

public class StepFormatter {
    private final String stepDescription;
    private final List<NamedArgument> arguments;
    private final List<ObjectFormatter<?>> formatters;

    public abstract static class Formatting<F, T> implements ObjectFormatter<T> {
        protected final F formatter;

        Formatting( F formatter ) {
            this.formatter = formatter;
        }

        public abstract String format( T o );

        public F getFormatter() {
            return formatter;
        }
    }

    public static class TypeBasedFormatting<T> extends Formatting<Formatter<T>, T> {
        private final Annotation[] annotations;

        public TypeBasedFormatting( Formatter<T> formatter, Annotation[] annotations ) {
            super( formatter );
            this.annotations = annotations;
        }

        @Override
        public String format( T o ) {
            return formatter.format( o, annotations );
        }
    }

    public static class ArgumentFormatting<F extends ArgumentFormatter<T>, T> extends Formatting<F, T> {
        private final String[] args;

        public ArgumentFormatting( F formatter, String... args ) {
            super( formatter );
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

    public static class TableFormatting<F extends TableFormatter> extends Formatting<F, Object> {

        private final Table tableAnnotation;
        private final String parameterName;
        private final Annotation[] annotations;

        public TableFormatting( F formatter, Table tableAnnotation, String parameterName, Annotation... annotations ) {
            super( formatter );
            this.tableAnnotation = tableAnnotation;
            this.parameterName = parameterName;
            this.annotations = annotations;
        }

        @Override
        public String format( Object o ) {
            return null;
        }

        public DataTable formatTable( Object o ) {
            return formatter.format( o, tableAnnotation, parameterName, annotations );
        }
    }

    public static class ChainedFormatting<T> extends Formatting<ObjectFormatter<T>, T> {

        private final List<Formatting<?, String>> formattings = Lists.newArrayList();

        public ChainedFormatting( ObjectFormatter<T> innerFormatting ) {
            super( innerFormatting );
        }

        @Override
        public String format( T o ) {
            String result = getFormatter().format( o );
            for( Formatting<?, String> formatting : formattings ) {
                try {
                    result = formatting.format( result );
                } catch( ClassCastException e ) {
                    throw new JGivenWrongUsageException( "Could not apply the formatter. " +
                            "When using multiple formatters on an argument, all but the last need to apply to strings.", e );
                }
            }

            return result;
        }

        public ChainedFormatting<T> addFormatting( Formatting<?, String> formatting ) {
            formattings.add( formatting );
            return this;
        }

    }

    public StepFormatter( String stepDescription, List<NamedArgument> arguments, List<ObjectFormatter<?>> formatters ) {
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
                    && ( formatters.get( i ) instanceof TableFormatting ) ) {
                DataTable dataTable = ( (TableFormatting) formatters.get( i ) ).formatTable( value );
                remainingArguments.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ),
                    dataTable ) );
            } else {
                remainingArguments.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ), formattedValue ) );
            }
        }
        return remainingArguments;
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

        ObjectFormatter<?> formatter = formatters.get( index );
        String formattedValue = formatUsingFormatterOrNull( formatter, value );
        String argumentName = WordUtil.fromSnakeCase( arguments.get( index ).name );

        formattedWords.add( Word.argWord( argumentName, defaultFormattedValue, formattedValue ) );
    }

    @SuppressWarnings( "unchecked" )
    private <T> String formatUsingFormatterOrNull( ObjectFormatter<T> argumentFormatter, Object value ) {
        if( argumentFormatter == null ) {
            return null;
        }

        return argumentFormatter.format( (T) value );
    }

    private static String toDefaultStringFormat( Object value ) {
        return new DefaultFormatter<Object>().format( value );
    }
}
