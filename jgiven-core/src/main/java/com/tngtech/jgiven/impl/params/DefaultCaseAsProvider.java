package com.tngtech.jgiven.impl.params;

import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.annotation.CaseAsProvider;
import com.tngtech.jgiven.format.ObjectFormatter;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.StepFormatter;
import com.tngtech.jgiven.report.model.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default case description provider that uses the value pattern
 */
public class DefaultCaseAsProvider implements CaseAsProvider {

    @Override
    public String as( String caseDescription, List<String> parameterNames, List<?> parameterValues ) {
        if( caseDescription.equals( CaseAs.NO_VALUE ) ) {
            return defaultDescription( parameterNames, parameterValues );
        }

        List<NamedArgument> namedArguments = convertToNamedArguments(parameterNames, parameterValues);
        List<ObjectFormatter<?>> formatters = getFormatters(Math.max(parameterNames.size(), parameterValues.size()));
        return new StepFormatter(caseDescription, namedArguments, formatters)
                .buildFormattedWordsIgnoringExtraArguments()
                .stream()
                .map(Word::getFormattedValue)
                .collect(Collectors.joining(" "));
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
    private List<NamedArgument> convertToNamedArguments(List<String> parameterNames, List<?> parameterValues){
        List<NamedArgument> namedArguments = new ArrayList<>();
        for (int i =0; i< parameterValues.size();i++){
            var parameterName = i < parameterNames.size()? parameterNames.get(i) :null;
            namedArguments.add(new NamedArgument(parameterName, parameterValues.get(i)));
        }
        return namedArguments;
    }

    private List<ObjectFormatter<?>> getFormatters(int amount) {
        return Stream.generate(() -> (ObjectFormatter<?>) Object::toString)
                .limit(amount)
                .collect(Collectors.toList());
    }
}
