package com.tngtech.jgiven.report.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.report.model.ArgumentInfo;
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
 * match in all cases to the same parameter
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
            if( firstWords.get( j ).isArg() && firstWords.get( j ).getArgumentInfo().isParameter() ) {
                continue;
            }
            if( !firstWords.get( j ).equals( words.get( j ) ) ) {
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

    private void reduceMatrix( ScenarioModel scenarioModel, List<CaseArguments> argumentMatrix ) {
        int nArguments = argumentMatrix.get( 0 ).arguments.size();
        List<String> derivedParams = Lists.newArrayList();
        for( int iArg = 0; iArg < nArguments; iArg++ ) {
            Set<String> currentSet = Sets.newLinkedHashSet();
            currentSet.addAll( argumentMatrix.get( 0 ).get( iArg ).params );
            for( int iCase = 1; iCase < argumentMatrix.size(); iCase++ ) {
                currentSet.retainAll( argumentMatrix.get( iCase ).get( iArg ).params );
            }
            if( currentSet.size() > 1 ) {
                log.warn( "Could not disambiguate case arguments for argument " + iArg + ". Values: " + currentSet );
            } else if( currentSet.isEmpty() ) {
                log.warn( "Could not identify parameter index for argument " + iArg );

                if( !allArgumentsAreEqual( argumentMatrix, iArg ) ) {
                    String parameterName = argumentMatrix.get( 0 ).get( iArg ).word.getArgumentInfo().getArgumentName();
                    derivedParams.add( parameterName );
                    for( int iCase = 0; iCase < argumentMatrix.size(); iCase++ ) {
                        CaseArguments caseArguments = argumentMatrix.get( iCase );
                        Word word = caseArguments.get( iArg ).word;
                        ArgumentInfo argumentInfo = word.getArgumentInfo();
                        argumentInfo.setParameterName( parameterName );
                        argumentInfo.setDerivedParameter( true );
                        caseArguments.caseModel.addArguments( word.value );
                    }
                    scenarioModel.addDerivedParameter( parameterName );
                }

                continue;
            }
            for( int iCase = 0; iCase < argumentMatrix.size(); iCase++ ) {
                Word word = argumentMatrix.get( iCase ).get( iArg ).word;
                word.getArgumentInfo().setParameterName( currentSet.iterator().next() );
            }
        }

    }

    private boolean allArgumentsAreEqual( List<CaseArguments> argumentMatrix, int iArg ) {
        Word word = argumentMatrix.get( 0 ).get( iArg ).word;
        boolean allWordsAreEqual = true;
        for( int iCase = 1; iCase < argumentMatrix.size(); iCase++ ) {
            Word word2 = argumentMatrix.get( iCase ).get( iArg ).word;
            if( !word.equals( word2 ) ) {
                allWordsAreEqual = false;
                break;
            }
        }
        return allWordsAreEqual;
    }

    static class ArgumentHolder {
        Word word;
        Set<String> params;
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

        private Set<String> getMatchingParameters( Word word ) {
            Set<String> matchingParameters = Sets.newLinkedHashSet();
            for( int i = 0; i < currentCase.arguments.size(); i++ ) {
                if( Objects.equal( word.value, currentCase.arguments.get( i ) ) ) {
                    if( i < scenarioModel.parameterNames.size() ) {
                        matchingParameters.add( scenarioModel.parameterNames.get( i ) );
                    }
                }
            }
            return matchingParameters;
        }
    }

}
