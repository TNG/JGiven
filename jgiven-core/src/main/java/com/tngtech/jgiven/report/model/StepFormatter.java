package com.tngtech.jgiven.report.model;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.DefaultFormatter;

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
            formattedWords.add( Word.argWord( arguments.get( i ).name, toDefaultStringFormat( value ), formattedValue ) );
        }
        return formattedWords;
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

    private String toDefaultStringFormat( Object value ) {
        return new DefaultFormatter<Object>().format( value );
    }
}
