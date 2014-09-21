package com.tngtech.jgiven.report.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.tngtech.jgiven.report.impl.CaseArgumentAnalyser.CollectPhase;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Word;

public class CaseDifferenceAnalyzer {

    public void analyze( ScenarioModel scenarioModel ) {
        if( scenarioModel.getScenarioCases().size() < 2 ) {
            return;
        }

        CollectPhase collectPhase = new CollectPhase( scenarioModel );
        scenarioModel.accept( collectPhase );

        List<Sequence> commonSequences = findCommonSequence( collectPhase.allWords );
        for( Sequence seq : commonSequences ) {
            seq.setDifferenceToWords();
        }
    }

    static class Sequence {
        List<Word> input;
        List<Integer> elements = new ArrayList<Integer>();

        int getLastIndex() {
            return elements.get( elements.size() - 1 );
        }

        boolean isAtEnd() {
            if( elements.isEmpty() ) {
                return input.isEmpty();
            }
            return getLastIndex() == input.size() - 1;
        }

        public void setDifferenceToWords() {
            for( Word word : input ) {
                word.setIsDifferent( true );
            }

            for( Integer i : elements ) {
                input.get( i ).setIsDifferent( false );
            }
        }

    }

    private static List<Sequence> findCommonSequence( List<List<Word>> input ) {
        List<Sequence> result = new ArrayList<Sequence>();

        for( List<Word> s : input ) {
            Sequence seq = new Sequence();
            seq.input = s;
            result.add( seq );
        }

        int[] startIndices = new int[input.size()];

        while( !someAtEnd( result ) ) {
            Searcher searcher = new Searcher( input, startIndices );
            int[] nextMatching = searcher.findNextMatching();
            if( nextMatching == null ) {
                break;
            }
            for( int i = 0; i < result.size(); i++ ) {
                result.get( i ).elements.add( nextMatching[i] );
            }
            startIndices = incAllByOne( nextMatching );

        }

        return result;
    }

    private static int[] incAllByOne( int[] matching ) {
        int[] result = new int[matching.length];
        for( int i = 0; i < result.length; i++ ) {
            result[i] = matching[i] + 1;
        }
        return result;
    }

    private static boolean someAtEnd( List<Sequence> result ) {
        for( Sequence s : result ) {
            if( s.isAtEnd() ) {
                return true;
            }
        }
        return false;
    }

    static class Searcher {
        List<List<Word>> input;
        int[] currentIndices;
        int currentRow;
        Word value;
        int[] startIndices;

        Searcher( List<List<Word>> input, int[] startIndices ) {
            this.input = input;
            this.startIndices = startIndices;
            currentIndices = Arrays.copyOf( startIndices, startIndices.length );
            initSearch();
        }

        private void initSearch() {
            value = input.get( 0 ).get( currentIndices[0] );
            currentRow = 1;
        }

        public int[] findNextMatching() {
            return findNext();
        }

        private int[] findNext() {
            while( currentRow < input.size() ) {
                if( currentRowAtEnd() ) {
                    if( !backTrack() ) {
                        return null;
                    }
                    continue;
                }

                if( getCurrentValue().equals( value ) ) {
                    currentRow++;
                } else {
                    currentIndices[currentRow] = getCurrentIndex() + 1;
                }
            }
            return currentIndices;
        }

        private boolean backTrack() {
            if( currentRow == 0 ) {
                currentIndices[currentRow] = getCurrentIndex() + 1;
                if( currentRowAtEnd() ) {
                    return false;
                }
                value = getCurrentValue();
                currentRow++;
                return true;
            }

            currentIndices[currentRow] = startIndices[currentRow];
            currentRow--;
            return backTrack();
        }

        private Word getCurrentValue() {
            return input.get( currentRow ).get( getCurrentIndex() );
        }

        private int getCurrentIndex() {
            return currentIndices[currentRow];
        }

        private boolean currentRowAtEnd() {
            return getCurrentIndex() == input.get( currentRow ).size();
        }
    }

}
