package com.tngtech.jgiven.report.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class CasesTableCalculatorTest {

    private static final long ARBITRARY_DURATION = 3278090000L;
    private final CasesTableCalculator calculator = new CasesTableCalculator();

    @Test
    public void collectCasesWithoutSpecials() {
        // given
        final ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDurationInNanos(ARBITRARY_DURATION);

        final ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));
        case2.setStatus(ExecutionStatus.SCENARIO_PENDING);
        case2.setDurationInNanos(ARBITRARY_DURATION);

        final ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        // when
        final CasesTable casesTable = calculator.collectCases(scenario);

        // then
        assertThat(casesTable.hasDescriptions()).isFalse();
        assertThat(casesTable.placeholders()).containsExactly("foo", "bar");
        assertThat(casesTable.rows())
                .hasSize(2)
                .satisfiesExactly(
                        caseRowOne -> {
                            assertThat(caseRowOne.rowNumber()).isEqualTo(1);
                            assertThat(caseRowOne.description()).isEmpty();
                            assertThat(caseRowOne.arguments()).containsExactly("1", "2");
                            assertThat(caseRowOne.status()).isEqualTo(ExecutionStatus.SUCCESS);
                            assertThat(caseRowOne.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowOne.errorMessage()).isEmpty();
                            assertThat(caseRowOne.stackTrace()).isEmpty();
                        },
                        caseRowTwo -> {
                            assertThat(caseRowTwo.rowNumber()).isEqualTo(2);
                            assertThat(caseRowTwo.description()).isEmpty();
                            assertThat(caseRowTwo.arguments()).containsExactly("3", "4");
                            assertThat(caseRowTwo.status()).isEqualTo(ExecutionStatus.SCENARIO_PENDING);
                            assertThat(caseRowTwo.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowTwo.errorMessage()).isEmpty();
                            assertThat(caseRowTwo.stackTrace()).isEmpty();
                        });
    }

    @Test
    public void collectCasesWithDescriptions() {
        // given
        final ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setDescription("First case");
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setStatus(ExecutionStatus.SUCCESS);
        case1.setDurationInNanos(ARBITRARY_DURATION);

        final ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setDescription("Second case");
        case2.setDerivedArguments(ImmutableList.of("3", "4"));
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDurationInNanos(ARBITRARY_DURATION);

        final ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        // when
        final CasesTable casesTable = calculator.collectCases(scenario);

        // then
        assertThat(casesTable.hasDescriptions()).isTrue();
        assertThat(casesTable.placeholders()).containsExactly("foo", "bar");
        assertThat(casesTable.rows())
                .hasSize(2)
                .satisfiesExactly(
                        caseRowOne -> {
                            assertThat(caseRowOne.rowNumber()).isEqualTo(1);
                            assertThat(caseRowOne.description()).hasValue("First case");
                            assertThat(caseRowOne.arguments()).containsExactly("1", "2");
                            assertThat(caseRowOne.status()).isEqualTo(ExecutionStatus.SUCCESS);
                            assertThat(caseRowOne.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowOne.errorMessage()).isEmpty();
                            assertThat(caseRowOne.stackTrace()).isEmpty();
                        },
                        caseRowTwo -> {
                            assertThat(caseRowTwo.rowNumber()).isEqualTo(2);
                            assertThat(caseRowTwo.description()).hasValue("Second case");
                            assertThat(caseRowTwo.arguments()).containsExactly("3", "4");
                            assertThat(caseRowTwo.status()).isEqualTo(ExecutionStatus.FAILED);
                            assertThat(caseRowTwo.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowTwo.errorMessage()).isEmpty();
                            assertThat(caseRowTwo.stackTrace()).isEmpty();
                        });
    }

    @Test
    public void collectCasesWithErrors() {

        // given
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        case1.setCaseNr(1);
        case1.setDerivedArguments(ImmutableList.of("1", "2"));
        case1.setStatus(ExecutionStatus.FAILED);
        case1.setDurationInNanos(ARBITRARY_DURATION);
        final String errorMessage = "java.lang.AssertionError:\nvalue 5 is not 12";
        case1.setErrorMessage(errorMessage);
        List<String> stackTrace = new ArrayList<>();
        stackTrace.add("exception in line 1");
        stackTrace.add("called in line 2");
        case1.setStackTrace(stackTrace);

        ScenarioCaseModel case2 = new ScenarioCaseModel();
        case2.setCaseNr(2);
        case2.setDerivedArguments(ImmutableList.of("3", "4"));
        case2.setStatus(ExecutionStatus.FAILED);
        case2.setDurationInNanos(ARBITRARY_DURATION);

        ScenarioModel scenario = new ScenarioModel();
        scenario.addCase(case1);
        scenario.addCase(case2);
        scenario.addDerivedParameter("foo");
        scenario.addDerivedParameter("bar");

        // when
        final CasesTable casesTable = calculator.collectCases(scenario);

        // then
        assertThat(casesTable.hasDescriptions()).isFalse();
        assertThat(casesTable.placeholders()).containsExactly("foo", "bar");
        assertThat(casesTable.rows())
                .hasSize(2)
                .satisfiesExactly(
                        caseRowOne -> {
                            assertThat(caseRowOne.rowNumber()).isEqualTo(1);
                            assertThat(caseRowOne.description()).isEmpty();
                            assertThat(caseRowOne.arguments()).containsExactly("1", "2");
                            assertThat(caseRowOne.status()).isEqualTo(ExecutionStatus.FAILED);
                            assertThat(caseRowOne.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowOne.errorMessage()).hasValue(errorMessage);
                            assertThat(caseRowOne.stackTrace()).containsAll(stackTrace);
                        },
                        caseRowTwo -> {
                            assertThat(caseRowTwo.rowNumber()).isEqualTo(2);
                            assertThat(caseRowTwo.description()).isEmpty();
                            assertThat(caseRowTwo.arguments()).containsExactly("3", "4");
                            assertThat(caseRowTwo.status()).isEqualTo(ExecutionStatus.FAILED);
                            assertThat(caseRowTwo.durationInNanos()).isEqualTo(ARBITRARY_DURATION);
                            assertThat(caseRowTwo.errorMessage()).isEmpty();
                            assertThat(caseRowTwo.stackTrace()).isEmpty();
                        });
    }
}
