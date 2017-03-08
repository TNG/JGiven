package com.tngtech.jgiven.impl.params;

import com.tngtech.jgiven.annotation.CaseDescriptionProvider;
import com.tngtech.jgiven.annotation.OrdinalCaseDescription;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Default case description provider that uses the value pattern
 */
public class DefaultOrdinalCaseDescriptionProvider implements CaseDescriptionProvider {

    @Override
    public String description( String caseDescription, List<String> parameterNames, List<?> parameterValues ) {
        if( caseDescription.equals( OrdinalCaseDescription.NO_VALUE ) ) {
            return defaultDescription( parameterNames, parameterValues );
        }
        int singleDollarCounter = 0;
        StringBuilder resultingDescription = new StringBuilder();
        for( int i = 0; i < caseDescription.length(); i++ ) {

            boolean dollarMatch = caseDescription.charAt( i ) == '$';
            boolean nextCharExists = ( i + 1 ) < caseDescription.length();
            boolean escapedDollarMatch = nextCharExists && caseDescription.charAt( i + 1 ) == '$';
            boolean namedArgumentMatch = nextCharExists && parameterNames.contains( nextName( caseDescription.substring( i + 1 ) ) );
            boolean enumArgumentMatch =
                    nextCharExists && parameterValues.size() > nextIndex( caseDescription.substring( i + 1 ), parameterValues.size() );
            boolean singleDollarCountIndexExists = singleDollarCounter < parameterValues.size();

            if( dollarMatch ) {
                if( escapedDollarMatch ) {
                    resultingDescription.append( '$' );
                    i += 1;

                } else if( namedArgumentMatch ) {
                    String argumentName = nextName( caseDescription.substring( i + 1 ) );
                    int argumentIndex = parameterNames.indexOf( argumentName );
                    resultingDescription.append( parameterValues.get( argumentIndex ) );
                    i += argumentName.length();

                } else if( enumArgumentMatch ) {
                    int argumentIndex = nextIndex( caseDescription.substring( i + 1 ), parameterValues.size() );
                    resultingDescription.append( parameterValues.get( argumentIndex ) );
                    i += Integer.toString( argumentIndex ).length();

                } else if( singleDollarCountIndexExists ) {
                    int argumentIndex = singleDollarCounter;
                    resultingDescription.append( parameterValues.get( argumentIndex ) );
                    singleDollarCounter += 1;

                } else {
                    resultingDescription.append( '$' );
                }
            } else {
                resultingDescription.append( caseDescription.charAt( i ) );
            }

        }

        return resultingDescription.toString();
    }

    private static String nextName( String description ) {
        StringBuilder result = new StringBuilder();
        for( int i = 0; i < description.length(); i++ ) {
            char c = description.charAt( i );
            if( Character.isJavaIdentifierPart( c ) && c != '$' ) {
                result.append( c );
            } else {
                break;
            }
        }
        return result.toString();
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

    public static String defaultDescription( List<String> parameterNames, List<?> parameterValues ) {
        StringBuilder sb = new StringBuilder();
        for( int i = 0; i < parameterValues.size(); i++ ) {
            if( i < parameterNames.size() ) {
                sb.append( parameterNames.get( i ) );
                sb.append( " = " );
            }
            sb.append( parameterValues.get( i ) );
            if( i != parameterValues.size() - 1 ) {
                sb.append( ", " );
            }
        }
        return sb.toString();
    }
}
