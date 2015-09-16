package com.tngtech.jgiven.report.html5;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.tags.FeatureHtml5Report;
import com.tngtech.jgiven.tags.FeatureTags;

@FeatureHtml5Report
@Description( "Tests against the generated HTML5 App using WebDriver" )
@RunWith( DataProviderRunner.class )
public class Html5ReportGeneratorTest extends
        JGivenScenarioTest<GivenJsonReports<?>, WhenHtml5ReportGenerator<?>, ThenHtml5ReportGenerator<?>> {

    @Test
    @FeatureTags
    @ExtendedDescription( "To reduce duplicated storage of tag data, the HTML reporter " +
            "generates a 'tags.js' file that contains all tags that appeared in all input files" )
    @Description( "the HTML report generator creates a 'tags.js' file" )
    public void the_HTML_report_generator_creates_a_tags_file() throws Exception {
        given().a_report_model()
            .and().scenario_$_has_tag_$_with_value_$( 1, "TestTag", "123" )
            .and().the_report_exist_as_JSON_file();

        when().the_HTML_Report_Generator_is_executed();

        then().a_file_$_exists_in_folder_$( "tags.js", "data" )
            .and().a_file_$_exists_in_folder_$( "metaData.js", "data" );
    }

}
