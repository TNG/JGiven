package com.tngtech.jgiven.report.analysis;

import com.tngtech.jgiven.relocated.guava.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser.JoinedArgs;
import com.tngtech.jgiven.report.model.AttachmentModel;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Word;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith( DataProviderRunner.class )
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

    @Test
    @DataProvider( {
            "foo, true, foo, true, false",
            "foo, false, foo, true, true",
            "foo, true, bar, true, true",
            "foo, false, bar, false, false"
    } )
    public void inline_attachments_are_handed_correctly( String firstValue, boolean firstShowDirectly, String secondValue,
            boolean secondShowDirectly, boolean expectedResult ) {
        AttachmentModel firstAttachment = new AttachmentModel();
        firstAttachment.setValue( firstValue );
        firstAttachment.setShowDirectly( firstShowDirectly );

        AttachmentModel secondAttachment = new AttachmentModel();
        secondAttachment.setValue( secondValue );
        secondAttachment.setShowDirectly( secondShowDirectly );

        assertThat( analyser.attachmentIsStructurallyDifferent( firstAttachment, secondAttachment ) ).isEqualTo( expectedResult );
    }

    @Test
    public void equal_data_tables_are_found() {
        List<List<String>> data = Lists.newArrayList();
        data.add( Lists.<String>newArrayList( "1" ) );

        DataTable dataTable = new DataTable( Table.HeaderType.HORIZONTAL, data );
        Word word = Word.argWord( "arg1", "foo", dataTable );

        DataTable dataTable2 = new DataTable( Table.HeaderType.HORIZONTAL, data );
        Word word2 = Word.argWord( "arg1", "foo", dataTable2 );

        List<List<Word>> cases = Lists.newArrayList();
        cases.add( Lists.<Word>newArrayList( word ) );
        cases.add( Lists.<Word>newArrayList( word2 ) );

        assertThat( analyser.getDifferentArguments( cases ).get( 0 ) ).isEmpty();

    }

    @Test
    public void nested_steps_are_considered_in_structure_analysis() {
        ScenarioCaseModel case0 = caseWithNestedStep("foo");
        ScenarioCaseModel case1 = caseWithNestedStep("bar");
        assertThat(analyser.stepsAreDifferent(case0, case1)).isTrue();
    }

    @Test
    public void arguments_of_nested_steps_are_collected() {
        Word word = Word.argWord("testName", "testValue", "testValue");
        ScenarioCaseModel case0 = caseWithNestedStep(word);
        ScenarioModel model = new ScenarioModel();
        model.addCase(case0);
        assertThat(analyser.collectArguments(model).get(0)).contains(word);

    }

    private ScenarioCaseModel caseWithNestedStep(String nestedStepName) {
        return caseWithNestedStep(new Word(nestedStepName));
    }

    private ScenarioCaseModel caseWithNestedStep(Word word) {
        ScenarioCaseModel case0 = new ScenarioCaseModel();
        StepModel step0 = new StepModel();
        StepModel nestedStep0 = new StepModel();
        nestedStep0.addWords(word);
        step0.addNestedStep(nestedStep0);
        case0.addStep(step0);
        return case0;
    }
}
