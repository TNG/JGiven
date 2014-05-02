package com.tngtech.jgiven.report.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tngtech.jgiven.report.model.ArgumentWord;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;

/**
 * Analyzes a report model and tries to infer which step method
 * arguments match to which case argument
 *
 */
public class CaseArgumentAnalyser {
    private static final Logger log = LoggerFactory.getLogger( CaseArgumentAnalyser.class );

    public void analyze( ReportModel model ) {
        for( ScenarioModel scenarioModel : model.scenarios ) {
            analyze( scenarioModel );
        }
    }

    public void analyze( ScenarioModel scenarioModel ) {
        CollectPhase collectPhase = new CollectPhase();
        scenarioModel.accept( collectPhase );

        try {
            reduceMatrix( collectPhase.argumentMatrix );
        } catch( IndexOutOfBoundsException e ) {
            log.info( "Scenario model " + scenarioModel.className + "." + scenarioModel.testMethodName + " has no homogene cases."
                    + " Cannot analyse argument cases" );
        }

    }

    private void reduceMatrix( List<List<ArgumentHolder>> argumentMatrix ) {
        int nArguments = argumentMatrix.get( 0 ).size();
        for( int iArg = 0; iArg < nArguments; iArg++ ) {
            Set<Integer> currentSet = Sets.newLinkedHashSet();
            currentSet.addAll( argumentMatrix.get( 0 ).get( iArg ).params );
            for( int iCase = 1; iCase < argumentMatrix.size(); iCase++ ) {
                currentSet.retainAll( argumentMatrix.get( iCase ).get( iArg ).params );
            }
            if( currentSet.size() > 1 ) {
                log.warn( "Could not disambiguate case arguments for argument " + iArg + ". Values: " + currentSet );
            } else if( currentSet.isEmpty() ) {
                log.warn( "Could not identify parameter index for argument " + iArg );
                continue;
            }
            for( int iCase = 0; iCase < argumentMatrix.size(); iCase++ ) {
                ArgumentWord word = argumentMatrix.get( iCase ).get( iArg ).word;
                word.setParameterIndex( currentSet.iterator().next() );
            }
        }

    }

    static class ArgumentHolder {
        ArgumentWord word;
        Set<Integer> params;
    }

    /**
     * Collect all possible argument matches.
     * This results in a set of possible case arguments for each step argument
     */
    static class CollectPhase extends ReportModelVisitor {
        List<List<ArgumentHolder>> argumentMatrix = Lists.newArrayList();
        List<ArgumentHolder> argumentsOfCurrentCase;
        ScenarioCaseModel currentCase;

        @Override
        public void visit( ScenarioCaseModel scenarioCase ) {
            currentCase = scenarioCase;
            argumentsOfCurrentCase = Lists.newArrayList();
            argumentMatrix.add( argumentsOfCurrentCase );
        }

        @Override
        public void visit( StepModel methodModel ) {
            for( Word word : methodModel.words ) {
                if( word.isArg() ) {
                    ArgumentHolder holder = new ArgumentHolder();
                    holder.word = (ArgumentWord) word;
                    holder.params = getMatchingIndices( word );
                    argumentsOfCurrentCase.add( holder );
                }
            }
        }

        private Set<Integer> getMatchingIndices( Word word ) {
            Set<Integer> matchingIndices = Sets.newLinkedHashSet();
            for( int i = 0; i < currentCase.arguments.size(); i++ ) {
                if( Objects.equal( word.value, currentCase.arguments.get( i ) ) ) {
                    matchingIndices.add( i );
                }
            }
            return matchingIndices;
        }
    }

}
