package com.tngtech.jgiven.report.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.*;
import com.tngtech.jgiven.format.table.TableFormatter;
import com.tngtech.jgiven.impl.util.WordUtil;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return buildFormattedWordsInternal((formattedWords, unusedArguments) -> {formattedWords.addAll(unusedArguments);return formattedWords;});
        } catch( JGivenWrongUsageException e ) {
            throw new JGivenWrongUsageException( e.getMessage() + ". Step definition: " + stepDescription );
        }
    }

    public List<Word> buildFormattedWordsIgnoringExtraArguments(){
        try {
            return buildFormattedWordsInternal((formattedWords, __) -> formattedWords);
        } catch( JGivenWrongUsageException e ) {
            throw new JGivenWrongUsageException( e.getMessage() + ". Step definition: " + stepDescription );
        }
    }

    private List<Word> buildFormattedWordsInternal(BiFunction<List<Word>, List<Word>,List<Word>> remainingArgumentHandler) {
        List<Word> formattedWords = Lists.newArrayList();
        Set<String> usedArguments = Sets.newHashSet();

        int singlePlaceholderCounter = 0;
        boolean dropNextWhitespace = false;
        StringBuilder currentWords = new StringBuilder();

        for( int i = 0; i < stepDescription.length(); i++ ) {

            boolean dollarMatch = stepDescription.charAt( i ) == '$';
            boolean nextCharExists = ( i + 1 ) < stepDescription.length();
            boolean escapedDollarMatch = nextCharExists && stepDescription.charAt( i + 1 ) == '$';

            if (dollarMatch && escapedDollarMatch){
                i+=1;
            }
            if( dollarMatch && !escapedDollarMatch) {
                String argumentName = nextCharExists ? nextName( stepDescription.substring( i + 1 ) ) : "";
                boolean namedArgumentExists = argumentName.length() > 0;
                boolean namedArgumentMatch = namedArgumentExists && isArgument( argumentName );
                boolean enumArgumentMatch =
                        nextCharExists && arguments.size() > nextIndex( stepDescription.substring( i + 1 ), arguments.size() );
                boolean singleDollarCountIndexExists = singlePlaceholderCounter < arguments.size();
                if( namedArgumentMatch ) {
                    int argumentIndex = getArgumentIndexByName( argumentName, 0 );
                    addArgumentByIndex( argumentIndex, currentWords, formattedWords, usedArguments );
                    i += argumentName.length();

                    // e.g $1
                } else if( enumArgumentMatch ) {
                    int argumentIndex = nextIndex( stepDescription.substring( i + 1 ), arguments.size() );
                    addArgumentByIndex( argumentIndex, currentWords, formattedWords, usedArguments );
                    i += Integer.toString( argumentIndex ).length();

                    // e.g $argumentNotKnown - gets replaced with running counter
                } else if( singleDollarCountIndexExists && namedArgumentExists ) {
                    int argumentIndex = singlePlaceholderCounter;
                    addArgumentByIndex( argumentIndex, currentWords, formattedWords, usedArguments );
                    singlePlaceholderCounter += 1;
                    i += argumentName.length();

                    // e.g $
                } else if( singleDollarCountIndexExists ) {
                    int argumentIndex = singlePlaceholderCounter;
                    addArgumentByIndex( argumentIndex, currentWords, formattedWords, usedArguments );
                    singlePlaceholderCounter += 1;

                    // e.g ($notKnown || $) && counter > argument.size
                } else {
                    formattedWords.add( new Word( '$' + argumentName ) );
                    i += argumentName.length();
                }

                // unfortunately we need this after every argument so the Joiner can use .join(' ') on the formattedWords
                dropNextWhitespace = true;

                // if no placeholder was detected, check dropNextWhitespace rule and append the next character
            } else {
                if (dropNextWhitespace && stepDescription.charAt( i ) == ' ') {
                    dropNextWhitespace = false;
                } else {
                    currentWords.append( stepDescription.charAt( i ) );
                }
            }
        }

        flushCurrentWord( currentWords, formattedWords, false );
        return remainingArgumentHandler.apply(formattedWords, getRemainingArguments(usedArguments));
    }

    /**
     * Greedy search for the next String from the start in the {@param description}
     * until a non JavaIdentifierPart or $ is found
     *
     * @param description the searchable {@link String}
     * @return a {@link String} consisting only of JavaIdentifiableParts
     */
    private static String nextName( String description ) {
        StringBuilder result = new StringBuilder();
        for(char c : description.toCharArray()) {
            if( Character.isJavaIdentifierPart( c ) && c != '$' ) {
                result.append( c );
            } else {
                break;
            }
        }
        return result.toString();
    }

    private int getArgumentIndexByName( String argumentName, int defaultIndex ) {
        for( int i = 0; i < arguments.size(); i++ ) {
            if( arguments.get( i ).name.equals( argumentName ) ) {
                return i;
            }
        }
        return defaultIndex;
    }

    private boolean isArgument( String argumentName ) {
        for( NamedArgument arg : arguments ) {
            if( arg.name.equals( argumentName ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looks up the argument by index via {@link #argumentIndexToWord(int)}, adds it to formattedWords and usedArguments
     * and flushes the previously accumulated text via {@link #flushCurrentWord(StringBuilder, List, boolean)}.
     *
     * @param index is searchable index
     * @param currentWords this {@link StringBuilder} holds the previously accumulated words
     * @param formattedWords this is the resulting List of Words
     * @param usedArguments this is the set which tracks which arguments were already used
     */
    private void addArgumentByIndex( int index, StringBuilder currentWords, List<Word> formattedWords, Set<String> usedArguments ) {
        flushCurrentWord( currentWords, formattedWords, true );
        Word argument = argumentIndexToWord( index );
        formattedWords.add( argument );
        usedArguments.add( argument.getArgumentInfo().getArgumentName() );
    }

    /**
     * Gets the argument based on the index, uses {@link #toDefaultStringFormat(Object)} and {@link #formatters} to format
     * the value and name of the Word accordingly
     *
     * @param index is the searchable index in {@link #arguments}
     * @return the {@link Word}
     */
    private Word argumentIndexToWord( int index ) {
        Object value = arguments.get( index ).value;
        String defaultFormattedValue = toDefaultStringFormat( value );

        ObjectFormatter<?> formatter = formatters.get( index );
        String formattedValue = formatUsingFormatterOrNull( formatter, value );
        String argumentName = WordUtil.fromSnakeCase( arguments.get( index ).name );

        return Word.argWord( argumentName, defaultFormattedValue, formattedValue );
    }

    /**
     * Appends the accumulated words to the resulting words. Trailing whitespace is removed because of the
     * postprocessing that inserts custom whitespace
     *
     * @param currentWords is the {@link StringBuilder} of the accumulated words
     * @param formattedWords is the list that is being appended to
     */
    private static void flushCurrentWord( StringBuilder currentWords, List<Word> formattedWords, boolean cutWhitespace ) {
        if( currentWords.length() > 0 ) {
            if( cutWhitespace && currentWords.charAt( currentWords.length() - 1 ) == ' ' ) {
                currentWords.setLength( currentWords.length() - 1 );
            }
            formattedWords.add( new Word( currentWords.toString() ) );
            currentWords.setLength( 0 );
        }
    }

    /**
     * Returns the next index of the argument by decrementing 1 from the possibly parsed number
     *
     * @param description this String will be searched from the start for a number
     * @param defaultIndex this will be returned if the match does not succeed
     * @return the parsed index or the defaultIndex
     */
    private static int nextIndex( String description, int defaultIndex ) {

        Pattern startsWithNumber = Pattern.compile( "(\\d+).*" );
        Matcher matcher = startsWithNumber.matcher( description );
        if( matcher.matches() ) {
            return Integer.parseInt( matcher.group( 1 ) ) - 1;
        }

        return defaultIndex;
    }

    private List<Word> getRemainingArguments( Set<String> usedArguments ) {
        List<Word> remainingArguments = Lists.newArrayList();
        for( int i = 0; i < arguments.size(); i++ ) {
            Object value = arguments.get( i ).value;
            String formattedValue = formatUsingFormatterOrNull( formatters.get( i ), value );

            if( !usedArguments.contains( arguments.get( i ).name ) ) {
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
        }
        return remainingArguments;
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
