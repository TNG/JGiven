package com.tngtech.jgiven.report.analysis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser.JoinedArgs;
import com.tngtech.jgiven.report.model.Word;

public class CaseArgumentAnalyserUnitTest {

    private CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();

    static final String[][] testInput1 = new String[][] {
        { "arg1", "arg2" },
        { "x", "x" },
        { "a", "a" } };

    @Test
    public void identical_arguments_should_be_joined() {
        List<List<JoinedArgs>> joinedArgs = analyser.joinEqualArguments( toArguments( testInput1 ) );
        assertThat( joinedArgs.get( 0 ) ).hasSize( 1 );
    }

    static final String[][] testInput2 = new String[][] {
        { "arg1", "arg2" },
        { "x", "y" },
        { "a", "a" } };

    @Test
    public void different_arguments_should_not_be_joined() {
        List<List<JoinedArgs>> joinedArgs = analyser.joinEqualArguments( toArguments( testInput2 ) );
        assertThat( joinedArgs.get( 0 ) ).hasSize( 2 );
    }

    static final String[][] testInput3 = new String[][] {
        { "arg1", "arg2", "arg3" },
        { "x", "y", "x" },
        { "a", "a", "a" } };

    @Test
    public void identical_arguments_should_be_joined_but_different_not() {
        List<List<JoinedArgs>> joinedArgs = analyser.joinEqualArguments( toArguments( testInput3 ) );
        assertThat( joinedArgs.get( 0 ) ).hasSize( 2 );
        assertThat( joinedArgs.get( 0 ).get( 0 ).words.get( 0 ).getFormattedValue() ).isEqualTo( "x" );
        assertThat( joinedArgs.get( 0 ).get( 0 ).words.get( 1 ).getFormattedValue() ).isEqualTo( "x" );
        assertThat( joinedArgs.get( 0 ).get( 1 ).words.get( 0 ).getFormattedValue() ).isEqualTo( "y" );
    }

    private List<List<Word>> toArguments( String[][] testInput ) {
        List<List<Word>> result = Lists.newArrayList();

        for( int i = 1; i < testInput.length; i++ ) {
            List<Word> row = Lists.newArrayList();
            result.add( row );
            for( int j = 0; j < testInput[i].length; j++ ) {
                String value = testInput[i][j];
                Word w = Word.argWord( testInput[0][j], value, value );
                row.add( w );
            }
        }

        return result;
    }
}
