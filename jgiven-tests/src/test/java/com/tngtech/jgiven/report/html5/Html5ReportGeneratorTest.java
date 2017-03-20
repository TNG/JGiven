package com.tngtech.jgiven.report.html5;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import javax.xml.bind.DatatypeConverter;

import com.tngtech.jgiven.annotation.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.GivenAttachments;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.tags.FeatureAttachments;
import com.tngtech.jgiven.tags.FeatureHtml5Report;
import com.tngtech.jgiven.tags.FeatureTags;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

@FeatureHtml5Report
@As( "HTML Report Generator" )
@Description( "Test that only checks the generated files of the HTML report generator" )
@RunWith( DataProviderRunner.class )
public class Html5ReportGeneratorTest extends
        JGivenScenarioTest<GivenReportModels<?>, WhenHtml5ReportGenerator<?>, ThenHtml5ReportGenerator<?>> {
    private static final String JSON_SAMPLE = "{" +
            "  \"foo\": \"bar\"" +
            "}";
    private static final String BINARY_SAMPLE = DatatypeConverter.printBase64Binary(DatatypeConverter.parseHexBinary( "89504E470D0A1A0A" ));

    @ScenarioStage
    GivenJsonReports<?> jsonReports;

    @ScenarioStage
    GivenAttachments<?> attachments;

    @Test
    @FeatureTags
    @ExtendedDescription( "To reduce duplicated storage of tag data, the HTML reporter " +
            "generates a 'tags.js' file that contains all tags that appeared in all input files" )
    @Description( "the HTML report generator creates a 'tags.js' file" )
    public void the_HTML_report_generator_creates_a_tags_file() throws Exception {
        given().a_report_model()
            .and().scenario_$_has_tag_$_with_value_$( 1, "TestTag", "123" );
        jsonReports
            .and().the_report_exist_as_JSON_file();

        when().the_HTML_Report_Generator_is_executed();

        then().a_file_$_exists_in_folder_$( "tags.js", "data" )
            .and().a_file_$_exists_in_folder_$( "metaData.js", "data" );
    }

    @Test
    public void the_title_of_the_HTML_report_can_be_configured() throws Exception {
        given().a_report_model();
        jsonReports
            .and().the_report_exist_as_JSON_file();

        when().the_HTML_Report_Generator_is_executed_with_title( "Test Title" );

        then().the_metaData_file_has_title_set_to( "Test Title" );
    }

    @Test
    @FeatureAttachments
    public void attachments_with_different_media_types_can_be_created() throws IOException {

        given().a_report_model();
        attachments
                .and().an_attachment_with_content_$_and_mediaType(JSON_SAMPLE, MediaType.JSON_UTF_8)
                .and().file_name("jsonfile")
                .and().an_attachment_with_binary_content_$_and_mediaType(BINARY_SAMPLE, MediaType.application( "octet-stream" ))
                .and().file_name("binary");
        given()
                .and().the_attachments_are_added_to_step_$_of_case_$(1,1);
        jsonReports
                .and().the_report_exist_as_JSON_file();

        when().the_HTML_Report_Generator_is_executed();

        String folder = "data/attachments/Test".replaceAll("/", Matcher.quoteReplacement(File.separator));
        then().a_file_$_exists_in_folder_$("jsonfile.json", folder)
                .with().content(JSON_SAMPLE)
                .and().a_file_$_exists_in_folder_$("binary.octet-stream", folder)
                .with().binary_content(BINARY_SAMPLE);
    }

}
