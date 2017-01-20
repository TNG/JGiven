package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.impl.ReportModelHolder;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.report.model.ReportModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;

import java.util.EnumSet;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;

/**
 * @since 0.15.0
 */
public class DynamicJGivenTest {

    /**
     * JGiven-specific factory method for creating dynamic JUnit 5 tests
     *
     * <h1>HIGHLY EXPERIMENTAL</h1>
     *
     * Most likely this method will change in future versions of JGiven, please don't
     * use this method for any serious projects, yet.
     *
     * @see DynamicTest#dynamicTest(String, Executable)
     * @param displayName the display name for the dynamic test; never
     *                    {@code null} or blank
     * @param executable the executable code block for the dynamic test;
     *                    never {@code null}
     */
    public static DynamicTest dynamicJGivenTest(String displayName, JGivenExecutable executable) {
        return DynamicTest.dynamicTest(displayName, executableWrapper(displayName, executable));
    }

    private static Executable executableWrapper(final String displayName, final JGivenExecutable executable) {
        return new Executable() {
            @Override
            public void execute() throws Throwable {
                ScenarioBase scenario = new ScenarioBase();
                ReportModel reportModel = ReportModelHolder.get().getReportModelOfCurrentThread();
                scenario.setModel(reportModel);
                scenario.startScenario(displayName);
                try {
                    executable.execute(scenario);

                    scenario.finished();
                    // ignore test when scenario is not implemented
                    Assumptions.assumeTrue( EnumSet.of( SUCCESS, FAILED ).contains( scenario.getScenarioModel().getExecutionStatus() ) );
                } catch( Exception e ) {
                    scenario.finished();
                    scenario.getExecutor().failed( e );
                    throw e;
                }
            }
        };
    }

}
