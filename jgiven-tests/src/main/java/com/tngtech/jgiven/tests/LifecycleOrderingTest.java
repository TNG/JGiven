package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.testng.ScenarioTestListener;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testng.annotations.Listeners;

import static com.tngtech.jgiven.tests.LifecycleOrderingTest.TestStage;

@Listeners( ScenarioTestListener.class )
@ExtendWith(JGivenReportExtractingExtension.class)
public class LifecycleOrderingTest extends ScenarioTestForTesting<TestStage, TestStage,TestStage> {


    @ScenarioStage
    private TestStage testStage;


    int testFrameWorkBeforeATestMethodCalled = 0;
    int testFrameworkAfterMethodCalled = 0;

    @BeforeEach
    @Before
    @org.testng.annotations.BeforeTest
    public void incrementTestFrameworkBeforeCounter() {
        testFrameWorkBeforeATestMethodCalled++;
    }

    @Test
    @org.testng.annotations.Test
    @org.junit.Test
    public void ostensibly_empty_test() {
        abuseScenarioToCommunicateMethodOrder(
                testFrameWorkBeforeATestMethodCalled > testStage.beforeScenarioCalled ?
                        OrderPossibility.BEFORE_FRAMEWORK_FIRST :
                        OrderPossibility.BEFORE_JGIVEN_FIRST);
        testStage.given().an_emptySetup();
        testStage.when().no_action_is_performed();
        testStage.then().nothing_happens();
    }

    @AfterEach
    @After
    @org.testng.annotations.AfterTest
    public void incrementTestFrameworkAfterCounter() {
        abuseScenarioToCommunicateMethodOrder(testFrameworkAfterMethodCalled < testStage.afterScenarioCalled ?
                OrderPossibility.AFTER_JGIVEN_FIRST :
                OrderPossibility.AFTER_FRAMEWORK_FIRST);
        testFrameworkAfterMethodCalled++;
    }

    private ScenarioModel modelAbusedForReporting;

    private enum OrderPossibility {
        BEFORE_FRAMEWORK_FIRST("framework before method called first"),
        BEFORE_JGIVEN_FIRST("framwork before method called second"),
        AFTER_JGIVEN_FIRST("framework after method called second"),
        AFTER_FRAMEWORK_FIRST("framework after method called first");

        OrderPossibility(String message) {
            this.message = message;
        }

        private final String message;
    }

    private void abuseScenarioToCommunicateMethodOrder(OrderPossibility orderPossibility) {
        addReportingScenario();
        var reportCase = new ScenarioCaseModel();
        reportCase.setDescription(orderPossibility.message);
        modelAbusedForReporting.addCase(reportCase);
    }

    private void addReportingScenario() {
        if ( modelAbusedForReporting == null ) {
            modelAbusedForReporting = new ScenarioModel();
            modelAbusedForReporting.setDescription("Model abused for Reporting");
            getScenario().getModel().getScenarios().add(modelAbusedForReporting);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    static class TestStage extends Stage<TestStage> {

        int beforeScenarioCalled = 0;
        int afterScenarioCalled = 0;
        @ScenarioRule
        public TestScenarioRule scenarioRule = new TestScenarioRule();

        TestStage an_emptySetup() {
            return this;
        }

        TestStage no_action_is_performed() {
            return this;
        }

        TestStage nothing_happens() {
            return this;
        }

        private class TestScenarioRule {
            public void before() {
                beforeScenarioCalled++;
            }

            public void after() {
                afterScenarioCalled++;
            }
        }
    }
}
