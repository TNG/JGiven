package com.tngtech.jgiven.report.asciireport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.report.ThenReportGenerator;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.tags.FeatureAsciiDocReport;

@RunWith( DataProviderRunner.class )
@FeatureAsciiDocReport
public class AsciiDocReportGeneratorTest extends
        JGivenScenarioTest<GivenJsonReports<?>, WhenReportGenerator<?>, ThenReportGenerator<?>> {

    @Test
    public void the_AsciiDoc_reporter_generates_an_index_file() throws IOException {
        given().a_report_model_as_JSON_file();
        when().the_asciidoc_reporter_is_executed();
        then().a_file_with_name_$_exists( "index.asciidoc" );
    }

}
