package com.tngtech.jgiven.cucumber;

import java.io.IOException;

import org.junit.Test;

import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.tags.FeatureCucumberReportConversion;

public class CucumberJsonReportConversionTest extends
        JGivenScenarioTest<GivenCucumberReport, WhenCucumberToJGivenConverter, ThenCucumberReport> {

    @Test
    @FeatureCucumberReportConversion
    public void Cucumber_report_JSON_files_can_be_parsed() throws IOException {
        given().a_Cucumber_report_as_JSON_file();
        when().then().the_file_can_be_parsed();
    }

    @Test
    @FeatureCucumberReportConversion
    public void Complete_Cucumber_report_can_be_converted() throws IOException {
        given().a_Cucumber_report_as_JSON_file();
        when().the_file_is_parsed()
            .and().it_is_converted_to_a_JGiven_report();
        then().the_result_is_correct();
    }

    @Test
    @FeatureCucumberReportConversion
    public void Cucumber_reports_can_be_converted_to_JGiven_reports() throws IOException {
        given().a_Cucumber_report();
        when().it_is_converted_to_a_JGiven_report();
        then().the_JGiven_report_matches_the_Cucumber_report();
    }

}
