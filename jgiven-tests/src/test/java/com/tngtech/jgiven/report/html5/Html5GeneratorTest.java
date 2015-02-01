package com.tngtech.jgiven.report.html5;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.WhenReportGenerator;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.FeatureAttachments;
import com.tngtech.jgiven.tags.FeatureHtml5Report;
import com.tngtech.jgiven.tags.Issue;

@FeatureHtml5Report
@RunWith( DataProviderRunner.class )
public class Html5GeneratorTest extends JGivenScenarioTest<GivenJsonReports<?>, WhenHtml5Report<?>, ThenHtml5Report<?>> {

    @ScenarioStage
    private WhenReportGenerator<?> whenReport;

    @ProvidedScenarioState
    static WebDriver webDriver;

    @BeforeClass
    public static void setupWebDriver() {
        webDriver = new PhantomJSDriver();
        webDriver.manage().window().setSize( new Dimension( 1280, 768 ) );
    }

    @AfterClass
    public static void closeWebDriver() {
        webDriver.close();
    }

    @Test
    public void the_welcome_page_of_the_HTML5_report_can_be_opened() throws Exception {
        given().a_report_model()
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML5_report_has_been_generated();

        when().the_index_page_is_opened();

        then().the_page_title_is( "Welcome" );
    }

    @Test
    public void the_statistics_on_the_welcome_page_of_the_HTML5_report_is_correct() throws Exception {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 3 )
            .and().step_$_of_case_$_has_status( 1, 1, StepStatus.FAILED )
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML5_report_has_been_generated();

        when().the_index_page_is_opened();

        then().the_page_statistics_line_contains_text( "3 scenarios, of which 2 were successful, 1 failed, and 0 are pending" );
    }

    @Test
    @Issue( "#47" )
    @DataProvider( {
        "true,  testtag-#42",
        "false, #42" } )
    public void clicking_on_tag_labels_opens_the_tag_page( boolean prependType, String tagName ) throws Exception {
        given().a_report_model()
            .and().scenario_$_has_tag_$_with_value_$( 1, "testtag", "#42" )
            .and().the_tag_has_prependType_set_to( prependType )
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML5_report_has_been_generated();

        when().the_All_Scenarios_page_is_opened()
            .and().the_tag_with_name_$_is_clicked( tagName );

        then().the_page_title_is( tagName );
    }

    @Test
    @FeatureAttachments
    public void attachments_appear_in_the_HTML5_report() throws Exception {
        String content = "Some Example Attachment\nwith some example content";
        given().a_report_model()
            .and().step_$_of_scenario_$_has_a_text_attachment_with_content( 1, 1, content )
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML5_report_has_been_generated();

        when().the_page_of_scenario_$_is_opened( 1 )
            .and().scenario_$_is_expanded( 1 );

        then().an_attachment_icon_exists()
            .and().the_content_of_the_referenced_attachment_is( content );

    }
}
