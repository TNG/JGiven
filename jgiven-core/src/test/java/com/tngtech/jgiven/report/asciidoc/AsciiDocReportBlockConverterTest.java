package com.tngtech.jgiven.report.asciidoc;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.annotation.Table;
import com.tngtech.jgiven.report.CasesTable;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.ReportStatistics;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.report.model.Word;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class AsciiDocReportBlockConverterTest {

    private AsciiDocReportBlockConverter converter;


    @Before
    public void setUp() {
        converter = new AsciiDocReportBlockConverter();
    }

    @Test
    public void convert_feature_header_without_description() {
        // arrange
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // act
        final String block = converter.convertFeatureHeaderBlock(
            "My first feature", statistics, null);

        // assert
        assertThat(block).hasLineCount(3)
            .containsSequence(
                "=== My first feature\n",
                "\n",
                "8 Successful, 21 Failed, 13 Pending, 42 Total (0s 0ms)");
    }

    @Test
    public void convert_feature_header_with_description() {
        // arrange
        final ReportStatistics statistics = new ReportStatistics();
        statistics.numScenarios = 42;
        statistics.numFailedScenarios = 21;
        statistics.numPendingScenarios = 13;
        statistics.numSuccessfulScenarios = 8;

        // act
        final String block = converter.convertFeatureHeaderBlock(
            "My first feature", statistics, "A very nice feature.");

        // assert
        assertThat(block).hasLineCount(7)
            .containsSequence(
                "=== My first feature\n",
                "\n",
                "8 Successful, 21 Failed, 13 Pending, 42 Total (0s 0ms)\n",
                "\n",
                "++++\n",
                "A very nice feature.\n",
                "++++");
    }

    @Test
    public void convert_scenario_header_without_tags_or_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.FAILED,
            1000300000L, tagNames, null);

        // assert
        assertThat(block).hasLineCount(3)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[FAILED] (1s 0ms)");
    }

    @Test
    public void convert_scenario_header_with_tags_and_no_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SCENARIO_PENDING,
            9000000L, tagNames, "");

        // assert
        assertThat(block).hasLineCount(5)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[PENDING] (0s 9ms)\n",
                "\n",
                "Tags: _Best Tag_");
    }

    @Test
    public void convert_scenario_header_with_description_and_no_tags() {
        // arrange
        List<String> tagNames = new ArrayList<>();

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SOME_STEPS_PENDING,
            2005000000L, tagNames, "Best scenario ever!!!");

        // assert
        assertThat(block).hasLineCount(7)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[PENDING] (2s 5ms)\n",
                "\n",
                "++++\n",
                "Best scenario ever!!!\n",
                "++++");
    }

    @Test
    public void convert_scenario_header_with_tags_and_description() {
        // arrange
        List<String> tagNames = new ArrayList<>();
        tagNames.add("Best Tag");

        // act
        String block = converter.convertScenarioHeaderBlock("my first scenario", ExecutionStatus.SUCCESS,
            3000000000L, tagNames, "Best scenario ever!!!");

        // assert
        assertThat(block).hasLineCount(9)
            .containsSequence(
                "=== My first scenario\n",
                "\n",
                "[SUCCESS] (3s 0ms)\n",
                "\n",
                "++++\n",
                "Best scenario ever!!!\n",
                "++++\n",
                "\n",
                "Tags: _Best Tag_");
    }

    @Test
    public void convert_case_header_without_parameters_and_description() {
        // arrange
        List<String> parameterNames = Collections.emptyList();
        List<String> parameterValues = Collections.emptyList();

        // act
        String block = converter.convertCaseHeaderBlock(1, parameterNames, parameterValues, null);

        // assert
        assertThat(block).isEqualTo("===== Case 1");
    }

    @Test
    public void convert_case_header_with_description_and_without_parameters() {
        // arrange
        List<String> parameterNames = Collections.singletonList("description");
        List<String> parameterValues = Collections.singletonList("First case");

        // act
        String block = converter.convertCaseHeaderBlock(1, parameterNames, parameterValues, "First case");

        // assert
        assertThat(block).hasLineCount(5).containsSequence(
            "===== Case 1 First case\n",
            "\n",
            "====\n",
            "description = First case\n",
            "====");
    }

    @Test
    public void convert_case_header_with_one_parameter() {
        // arrange
        List<String> parameterNames = Collections.singletonList("foo");
        List<String> parameterValues = Collections.singletonList("42");

        // act
        String block = converter.convertCaseHeaderBlock(2, parameterNames, parameterValues, null);

        // assert
        assertThat(block).hasLineCount(5).containsSequence(
            "===== Case 2\n",
            "\n",
            "====\n",
            "foo = 42\n",
            "====");
    }

    @Test
    public void convert_case_header_with_two_parameters() {
        // arrange
        List<String> parameterNames = new ArrayList<>();
        parameterNames.add("foo");
        parameterNames.add("bar");
        List<String> parameterValues = new ArrayList<>();
        parameterValues.add("42");
        parameterValues.add("on");

        // act
        String block = converter.convertCaseHeaderBlock(2, parameterNames, parameterValues, null);

        // assert
        assertThat(block).hasLineCount(5).containsSequence(
            "===== Case 2\n",
            "\n",
            "====\n",
            "foo = 42, bar = on\n",
            "====");
    }

    @Test
    public void convert_step_without_description() {
        // arrange
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("a coffee machine"));

        // act
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, 3899, null,
            false, null, false);

        // assert
        assertThat(block).isEqualTo("* [.jg-introWord]*Given* a coffee machine");
    }

    @Test
    public void convert_step_with_description() {
        // arrange
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("a coffee machine"));

        // act
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
            "It is a brand new machine.", false, null, false);

        // assert
        assertThat(block).hasLineCount(2).containsSequence(
            "* [.jg-introWord]*Given* a coffee machine +\n",
            "  _+++It is a brand new machine.+++_");
    }

    @Test
    public void convert_failed_step_without_description() {
        // arrange
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // act
        String block = converter.convertStepBlock(0, words, StepStatus.FAILED, 3000899,
            null, true, null, false);

        // assert
        assertThat(block).isEqualTo("* [.jg-introWord]*Given* [.right]#[FAILED] (0s 3ms)#");
    }

    @Test
    public void convert_first_step_in_section() {
        // arrange
        List<Word> words = Collections.singletonList(Word.introWord("given"));

        // act
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
            null, false, "First section", false);

        // assert
        assertThat(block).isEqualTo(".First section\n" +
            "* [.jg-introWord]*Given*");
    }


    @Test
    public void convert_step_with_simple_argument() {
        // arrange
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("a coffee machine with"),
            Word.argWord("ncoffees", "0", "0"),
            new Word("coffees"));

        // act
        String block =
            converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
                null,
                false, null, false);

        // assert
        assertThat(block).isEqualTo(
            "* [.jg-introWord]*Given* a coffee machine with [.jg-argument]_0_ coffees");
    }

    @Test
    public void convert_step_with_multiline_argument() {
        // arrange
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("a coffee machine with"),
            Word.argWord("description", "0", "very nice text\nand also more text"));

        // act
        String block =
            converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
                null,
                false, null, false);

        // assert
        assertThat(block).hasLineCount(7)
            .containsSequence(
                "* [.jg-introWord]*Given* a coffee machine with\n",
                "+\n",
                "[.jg-argument]\n",
                "....\n",
                "very nice text\n",
                "and also more text\n",
                "....");
    }

    @Test
    public void convert_step_with_parameter() {
        // arrange
        Word ncoffees = Word.argWord("coffee count", "0", "0");
        ncoffees.getArgumentInfo().setParameterName("coffee count");

        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("a coffee machine with"),
            ncoffees,
            new Word("coffees"));

        // act
        String block =
            converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
                null,
                false, null, false);

        // assert
        assertThat(block).isEqualTo(
            "* [.jg-introWord]*Given* a coffee machine with [.jg-argument]*<coffee count>* coffees");
    }

    @Test
    public void convert_step_with_data_table_with_horizontal_header() {
        // arrange
        ImmutableList<List<String>> productsTable = ImmutableList.of(
            ImmutableList.of("product", "price"),
            ImmutableList.of("apples", "23"),
            ImmutableList.of("pears", "42")
        );
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.HORIZONTAL, productsTable)));

        // act
        String block =
            converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
                null,
                false, null, false);

        // assert
        assertThat(block).hasLineCount(8)
            .containsSequence(
                "* [.jg-introWord]*Given* the products\n",
                "+\n",
                "[.jg-argumentTable%header,cols=\"1,1\"]\n",
                "|===\n",
                "|product|price\n",
                "|apples|23\n",
                "|pears|42\n",
                "|===");
    }

    @Test
    public void convert_step_with_data_table_vertical_header() {
        // arrange
        ImmutableList<List<String>> productsTable = ImmutableList.of(
            ImmutableList.of("product", "apples", "pears"),
            ImmutableList.of("price", "23", "42")
        );
        List<Word> words = ImmutableList.of(
            Word.introWord("given"),
            new Word("the products"),
            Word.argWord("products", productsTable.toString(),
                new DataTable(Table.HeaderType.VERTICAL, productsTable)));

        // act
        String block = converter.convertStepBlock(0, words, StepStatus.PASSED, 3899,
            null,
            false, null, false);

        // assert

        assertThat(block).hasLineCount(7)
            .containsSequence(
                "* [.jg-introWord]*Given* the products\n",
                "+\n",
                "[.jg-argumentTable,cols=\"h,1,1\"]\n",
                "|===\n",
                "|product|apples|pears\n",
                "|price|23|42\n",
                "|===");
    }

    @Test
    public void convert_cases_table_without_descriptions() {
        // arrange
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        CasesTable casesTable = new CasesTableImpl(scenario);

        // act
        String block = converter.convertCasesTableBlock(casesTable);

        // assert
        assertThat(block).hasLineCount(7)
            .containsSequence(
                ".Cases\n",
                "[.jg-casesTable%header,cols=\"h,1,1,>1\"]\n",
                "|===\n",
                "| # | foo | bar | Status\n",
                "| 1 | 1 | 2 | SUCCESS\n",
                "| 2 | 3 | 4 | FAILED\n",
                "|===");
    }

    @Test
    public void convert_cases_table_with_descriptions() {
        // arrange
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setDescription("First case");

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));
        case2.setDescription("Second case");

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        CasesTable casesTable = new CasesTableImpl(scenario);

        // act
        String block = converter.convertCasesTableBlock(casesTable);

        // assert
        assertThat(block).hasLineCount(7)
            .containsSequence(
                ".Cases\n",
                "[.jg-casesTable%header,cols=\"h,1,1,1,>1\"]\n",
                "|===\n",
                "| # | Description | foo | bar | Status\n",
                "| 1 | First case | 1 | 2 | SUCCESS\n",
                "| 2 | Second case | 3 | 4 | FAILED\n",
                "|===");
    }
}