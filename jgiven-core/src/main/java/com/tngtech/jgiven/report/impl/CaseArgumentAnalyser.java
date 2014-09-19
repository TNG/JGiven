package com.tngtech.jgiven.report.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

/**
 * Analyzes a report model and tries to infer which step method arguments match to which case argument.
 *
 * This is done by comparing all cases of a scenario and find out which method arguments
 * match in all cases to the same parameter.
 *
 * The algorithm is rather complex, but I could not find an easier one yet.
 *
 */
public class CaseArgumentAnalyser {
    private static final Logger log = LoggerFactory.getLogger( CaseArgumentAnalyser.class );

    public void analyze( ReportModel model ) {
        for( ScenarioModel scenarioModel : model.getScenarios() ) {
            analyze( scenarioModel );
        }
    }

    public void analyze( ScenarioModel scenarioModel ) {
        if( scenarioModel.getScenarioCases().size() < 2 ) {
            return;
        }
        CollectPhase collectPhase = new CollectPhase( scenarioModel );
        scenarioModel.accept( collectPhase );

        try {
            reduceMatrix( scenarioModel, collectPhase.argumentMatrix );
            scenarioModel.setCasesAsTable( allStepsEqual( collectPhase.allWords ) );
        } catch( IndexOutOfBoundsException e ) {
            log.info( "Scenario model " + scenarioModel.className + "." + scenarioModel.testMethodName + " has no homogene cases."
                    + " Cannot analyse argument cases" );
            scenarioModel.setCasesAsTable( false );
        }

    }

    private boolean allStepsEqual( List<List<Word>> allWords ) {
        List<Word> firstWords = allWords.get( 0 );
        for( int i = 1; i < allWords.size(); i++ ) {
            if( !wordsAreEqual( firstWords, allWords.get( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean wordsAreEqual( List<Word> firstWords, List<Word> words ) {
        if( firstWords.size() != words.size() ) {
            return false;
        }
        for( int j = 0; j < words.size(); j++ ) {
            Word firstWord = firstWords.get( j );
            Word word = words.get( j );
            if( firstWord.isArg() && word.isArg()
                    && Objects.equal( firstWord.getArgumentInfo().getArgumentName(), word.getArgumentInfo().getArgumentName() ) ) {
                continue;
            }
            if( !firstWord.equals( word ) ) {
                return false;
            }
        }
        return true;
    }

    private static final class CaseArguments {
        final ScenarioCaseModel caseModel;
        final List<ArgumentHolder> arguments;

        private CaseArguments( ScenarioCaseModel model, List<ArgumentHolder> arguments ) {
            this.caseModel = model;
            this.arguments = arguments;
        }

        public ArgumentHolder get( int i ) {
            return arguments.get( i );
        }
    }

    static class ParameterReplacement {
        List<Word> arguments;
        ParameterMatch match;
        String replacementName;
        boolean isStepParameterName;

        public void updateToStepParameterName( Set<String> usedNames ) {
            String name = arguments.get( 0 ).getArgumentInfo().getArgumentName();

            int i = 1;
            String suffix = "";
            while( usedNames.contains( name + suffix ) ) {
                i++;
                suffix = "" + i;
            }

            replacementName = name + suffix;
            usedNames.add( replacementName );
            isStepParameterName = true;
        }
    }

    private void reduceMatrix( ScenarioModel scenarioModel, List<CaseArguments> argumentMatrix ) {
        int nArguments = argumentMatrix.get( 0 ).arguments.size();
        Map<String, ParameterReplacement> usedParameters = Maps.newLinkedHashMap();
        List<ParameterReplacement> parameterReplacements = Lists.newArrayList();
        Set<String> usedNames = Sets.newHashSet();

        for( int iArg = 0; iArg < nArguments; iArg++ ) {
            List<Word> arguments = getArgumentsOfAllCases( argumentMatrix, iArg );

            if( allArgumentsAreEqual( arguments ) ) {
                continue;
            }

            ParameterReplacement replacement = new ParameterReplacement();
            replacement.arguments = arguments;
            parameterReplacements.add( replacement );

            Collection<ParameterMatch> parameterMatches = getPossibleParameterNames( argumentMatrix, iArg );

            if( !parameterMatches.isEmpty() ) {
                Iterator<ParameterMatch> iterator = parameterMatches.iterator();
                ParameterMatch match = iterator.next();
                replacement.match = match;
                replacement.replacementName = match.parameter;

                if( usedParameters.containsKey( match.parameter ) ) {
                    ParameterReplacement usedReplacement = usedParameters.get( match.parameter );
                    if( match.formattedValueMatches && !usedReplacement.match.formattedValueMatches ) {
                        usedReplacement.updateToStepParameterName( usedNames );
                    }
                }

                usedNames.add( replacement.replacementName );
                usedParameters.put( match.parameter, replacement );

                if( iterator.hasNext() ) {
                    log.debug( "Multiple parameter matches found for argument " + iArg + ": " + parameterMatches
                            + ". Took the first one." );
                }
            } else {
                replacement.updateToStepParameterName( usedNames );
            }
        }

        for( ParameterReplacement replacement : parameterReplacements ) {
            scenarioModel.addDerivedParameter( replacement.replacementName );
            for( int i = 0; i < replacement.arguments.size(); i++ ) {
                Word word = replacement.arguments.get( i );
                word.getArgumentInfo().setParameterName( replacement.replacementName );
                word.getArgumentInfo().setDerivedParameter( replacement.isStepParameterName );
                scenarioModel.getCase( i ).addDerivedArguments( word.getFormattedValue() );
            }
        }

    }

    private Collection<ParameterMatch> getPossibleParameterNames( List<CaseArguments> argumentMatrix, int iArg ) {
        Map<String, ParameterMatch> result = toMap( argumentMatrix.get( 0 ).arguments.get( iArg ).params );

        for( int i = 1; i < argumentMatrix.size(); i++ ) {
            Map<String, ParameterMatch> map = toMap( argumentMatrix.get( i ).arguments.get( iArg ).params );
            for( String key : Lists.newArrayList( result.keySet() ) ) {
                if( !map.containsKey( key ) ) {
                    result.remove( key );
                } else {
                    result.get( key ).formattedValueMatches &= map.get( key ).formattedValueMatches;
                }
            }
        }
        return result.values();
    }

    private Map<String, ParameterMatch> toMap( Set<ParameterMatch> params ) {
        Map<String, ParameterMatch> result = Maps.newLinkedHashMap();
        for( ParameterMatch match : params ) {
            result.put( match.parameter, match );
        }
        return result;
    }

    private List<Word> getArgumentsOfAllCases( List<CaseArguments> argumentMatrix, int iArg ) {
        List<Word> result = Lists.newArrayList();
        for( int iCase = 0; iCase < argumentMatrix.size(); iCase++ ) {
            result.add( argumentMatrix.get( iCase ).get( iArg ).word );
        }
        return result;
    }

    private boolean allArgumentsAreEqual( List<Word> arguments ) {
        Word firstWord = arguments.get( 0 );
        for( int i = 1; i < arguments.size(); i++ ) {
            Word word = arguments.get( i );
            if( !firstWord.equals( word ) ) {
                return false;
            }
        }
        return true;
    }

    static class ParameterMatch {
        String parameter;
        int index;
        boolean formattedValueMatches;
    }

    static class ArgumentHolder {
        Word word;
        Set<ParameterMatch> params;
    }

    /**
     * Collect all possible argument matches.
     * This results in a set of possible case arguments for each step argument
     */
    static class CollectPhase extends ReportModelVisitor {
        List<CaseArguments> argumentMatrix = Lists.newArrayList();
        List<ArgumentHolder> argumentsOfCurrentCase;
        List<List<Word>> allWords = Lists.newArrayList();
        List<Word> allWordsOfCurrentCase;
        ScenarioCaseModel currentCase;
        final ScenarioModel scenarioModel;

        public CollectPhase( ScenarioModel model ) {
            this.scenarioModel = model;
        }

        @Override
        public void visit( ScenarioCaseModel scenarioCase ) {
            currentCase = scenarioCase;
            argumentsOfCurrentCase = Lists.newArrayList();
            argumentMatrix.add( new CaseArguments( currentCase, argumentsOfCurrentCase ) );
            allWordsOfCurrentCase = Lists.newArrayList();
            allWords.add( allWordsOfCurrentCase );
        }

        @Override
        public void visit( StepModel methodModel ) {
            for( Word word : methodModel.words ) {
                if( word.isArg() ) {
                    ArgumentHolder holder = new ArgumentHolder();
                    holder.word = word;
                    holder.params = getMatchingParameters( word );
                    argumentsOfCurrentCase.add( holder );
                }
                allWordsOfCurrentCase.add( word );
            }
        }

        private Set<ParameterMatch> getMatchingParameters( Word word ) {
            Set<ParameterMatch> matchingParameters = Sets.newLinkedHashSet();
            for( int i = 0; i < currentCase.getExplicitArguments().size(); i++ ) {
                String argumentValue = currentCase.getExplicitArguments().get( i );
                if( Objects.equal( word.getValue(), argumentValue ) ) {
                    if( i < scenarioModel.getExplicitParameters().size() ) {
                        ParameterMatch match = new ParameterMatch();
                        match.index = i;
                        match.parameter = scenarioModel.getExplicitParameters().get( i );
                        match.formattedValueMatches = Objects.equal( word.getFormattedValue(), argumentValue );
                        matchingParameters.add( match );
                    }
                }
            }
            return matchingParameters;
        }
    }

}
