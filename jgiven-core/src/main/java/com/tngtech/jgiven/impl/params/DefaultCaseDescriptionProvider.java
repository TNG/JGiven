package com.tngtech.jgiven.impl.params;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tngtech.jgiven.annotation.CaseDescription;
import com.tngtech.jgiven.annotation.CaseDescriptionProvider;

/**
 * Default case description provider that uses the value pattern
 *
 * @deprecated As since v0.16.0 this is deprecated to remove the ambiguity of the argument enumeration
 *             an start from $1. Please use {@link DefaultCaseAsProvider} instead.
 */
@Deprecated public class DefaultCaseDescriptionProvider implements CaseDescriptionProvider {
    private static final Pattern pattern = Pattern.compile( "\\$(\\d+|\\$)?" );

    @Override
    public String description( String value, List<String> parameterNames, List<?> parameterValues ) {
        if( value.equals( CaseDescription.NO_VALUE ) ) {
            return defaultDescription( parameterNames, parameterValues );
        }

        Matcher matcher = pattern.matcher( value );

        int placeHolderCounter = 0;

        StringBuffer result = new StringBuffer();
        while( matcher.find() ) {
            String group = matcher.group( 1 );
            String replacement;
            if( group == null ) {
                replacement = String.valueOf( parameterValues.get( placeHolderCounter ) );
                placeHolderCounter++;
            } else {
                if( group.equals( "$" ) ) {
                    replacement = "\\$";
                } else {
                    int i = Integer.parseInt( group );
                    replacement = Matcher.quoteReplacement( String.valueOf( parameterValues.get( i ) ) );
                }
            }

            matcher.appendReplacement( result, replacement );
        }

        matcher.appendTail( result );

        return result.toString();
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
