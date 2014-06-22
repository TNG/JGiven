package com.tngtech.jgiven.report.html;

import static com.google.common.collect.Maps.newLinkedHashMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.report.model.ReportModel;

@RunWith( DataProviderRunner.class )
public class HtmlWriterTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {

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
    public void HTML_report_is_correctly_generated_for_scenarios( int a, int b, int expectedResult ) {
        scenario.startScenario( "values can be multiplied" );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        scenario.finished();
        ReportModel model = scenario.getModel();
        String string = HtmlWriter.toString( model.getLastScenarioModel() );
        assertThat( string.replace( '\n', ' ' ) ).matches( ".*"
                + "<h3>Values can be multiplied</h3>.*"
                + "<li><span class='introWord'>Given</span> <span class='argument '>" + a + "</span>.*and.*" + b + ".*</li>.*"
                + "<li><span class='introWord'>When</span> both values are multiplied with each other.*</li>.*"
                + "<li><span class='introWord'>Then</span> the result is.*<span class='argument '>" + expectedResult + "</span>.*</li>.*"
                + "<div class='topRight passed'>Passed</div>"
                + ".*" );
    }

    @DataProvider
    public static Object[][] testArguments() {
        return new Object[][] {
            { "a", "b" },
        };
    }

    @Test
    public void tests_with_arguments_generate_cases() throws Exception {
        LinkedHashMap<String, Object> args = newLinkedHashMap();
        args.put( "paramA", 1 );
        args.put( "paramB", 'b' );

        scenario.getExecutor().startScenario( getClass().getMethod( "tests_with_arguments_generate_cases" ), args );

        when().both_values_are_multiplied_with_each_other();

        scenario.finished();
        ReportModel model = scenario.getModel();
        String string = HtmlWriter.toString( model.getLastScenarioModel() );
        assertThat( string.replace( '\n', ' ' ) ).matches( ".*<h4>Case 1: paramA = 1, paramB = b</h4>.*" );
    }

}
