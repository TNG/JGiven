package com.tngtech.jgiven.testframework;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.impl.util.ReflectionUtil;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.ThenReportModel;
import com.tngtech.jgiven.tests.TestScenarioRepository.TestScenario;

public class ThenTestFramework<SELF extends ThenTestFramework<?>> extends ThenReportModel<SELF> {
    @ExpectedScenarioState
    protected TestScenario testScenario;

    @ExpectedScenarioState
    TestExecutionResult result;

    public void the_test_is_ignored() {
        // this is actually not correct, because it depends on the JUnit executor whether
        // a test is ignored if an AssumptionException is thrown.
        // The standard JUnit executor will report the test as passed and not ignored,
        // we thus only test for not failed here
        the_test_passes();
    }

    public void the_test_passes() {
        assertThat( result.getFailureCount() ).as( "failure count" ).isEqualTo( 0 );
    }

    public SELF the_test_fails() {
        assertThat( result.getFailureCount() ).as( "failure count" ).isGreaterThan( 0 );
        return self();
    }

    public void the_test_fails_with_message( String expectedMessage ) {
        the_test_fails();
        assertThat( result.getFailureMessage( 0 ) ).as( "failure message" ).isEqualTo( expectedMessage );
    }

    public SELF the_report_model_contains_one_scenario_for_each_test_method() {
        Method[] declaredMethods = testScenario.testClass.getDeclaredMethods();
        List<Method> nonStaticMethods = ReflectionUtil.getNonStaticMethod( testScenario.testClass.getDeclaredMethods() );
        assertThat( reportModel.getScenarios() ).hasSize( nonStaticMethods.size() );
        return self();
    }

    public SELF each_scenario_contains_$_cases( int nParameters ) {
        for( ScenarioModel scenario : reportModel.getScenarios() ) {
            assertThat( scenario.getScenarioCases() ).hasSize( nParameters );
        }
        return self();
    }
}
