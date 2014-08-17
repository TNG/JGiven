package com.tngtech.jgiven.report.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.base.ScenarioTestBase;

/**
 * Please note that we do explicitly <strong>not</strong> use the ScenarioTest class for JUnit,
 * as this would require a dependency to jgiven-junit, which we have to avoid
 * here.
 */
@RunWith( DataProviderRunner.class )
public class PlainTextReporterTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {
    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
            { 5, 6, 30 },
            { 2, 2, 4 },
            { -5, 1, -5 },
        };
    }

    @Test
    @UseDataProvider( "testData" )
    public void parameters_are_reported_correctly(int a, int b, int expectedResult) throws Exception {
        getScenario().startScenario( "values can be multiplied" );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string )
            .contains( "Given " + a + " and " + b )
            .contains( "When both values are multiplied with each other" )
            .contains( "Then the result is " + expectedResult );
    }

    @Test
    public void plain_text_report_works_as_expected() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );

        given().something()
            .and().something_else();

        when().something_happens();

        then().something_has_happend()
            .but().something_else_not();

        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string )
            .contains( ""
                    + " Scenario: Test\n"
                    + "\n"
                    + "   Given something\n"
                    + "     And something else\n"
                    + "    When something happens\n"
                    + "    Then something has happend\n"
                    + "     But something else not"
            );
    }
}
