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
    public void test() throws IOException {
        given().a_Cucumber_report();
    }

}
