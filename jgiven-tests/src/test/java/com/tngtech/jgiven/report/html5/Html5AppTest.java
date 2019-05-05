package com.tngtech.jgiven.report.html5;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.DriverManagerType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.JGivenScenarioTest;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.json.GivenJsonReports;
import com.tngtech.jgiven.report.model.GivenReportModels;
import com.tngtech.jgiven.report.model.StepStatus;
import com.tngtech.jgiven.tags.BrowserTest;
import com.tngtech.jgiven.tags.FeatureAttachments;
import com.tngtech.jgiven.tags.FeatureHtml5Report;
import com.tngtech.jgiven.tags.FeatureTags;
import com.tngtech.jgiven.tags.FeatureTagsWithCustomStyle;
import com.tngtech.jgiven.tags.Issue;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

@BrowserTest
@FeatureHtml5Report
@As( "HTML App" )
@Description( "Tests against the generated HTML5 App using WebDriver" )
@RunWith( DataProviderRunner.class )
public class Html5AppTest extends JGivenScenarioTest<GivenReportModels<?>, WhenHtml5App<?>, ThenHtml5App<?>> {

    @ScenarioStage
    private GivenJsonReports<?> jsonReports;

    @ScenarioStage
    private WhenHtml5ReportGenerator<?> whenReport;

    @ProvidedScenarioState
    static WebDriver webDriver;

    @BeforeClass
    public static void setupWebDriver() {
        ChromeDriverManager.getInstance(DriverManagerType.CHROME).setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("window-size=1280x768");

        webDriver = new ChromeDriver(options);

        webDriver.manage().window().setSize( new Dimension( 1280, 768 ) );
    }

    @AfterClass
    public static void closeWebDriver() {
        webDriver.close();
    }

    @Test
    public void the_welcome_page_of_the_HTML5_report_can_be_opened() throws Exception {
        given().a_report_model();
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_index_page_is_opened();

        then().the_page_title_is( "Welcome" );
    }

    @Test
    public void the_statistics_on_the_welcome_page_of_the_HTML5_report_is_correct() throws Exception {
        given().a_report_model()
            .and().the_report_has_$_scenarios( 3 )
            .and().step_$_of_case_$_has_status( 1, 1, StepStatus.FAILED );
        jsonReports
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_index_page_is_opened();

        then().the_page_statistics_line_contains_text( "3 Total" )
            .and().the_page_statistics_line_contains_text( "2 Successful" )
            .and().the_page_statistics_line_contains_text( "1 Failed" )
            .and().the_page_statistics_line_contains_text( "0 Pending" );
    }

    @Test
    @FeatureTags
    @Issue( "#47" )
    @DataProvider( {
        "true,  testtag-#42",
        "false, #42" } )
    public void clicking_on_tag_labels_opens_the_tag_page( boolean prependType, String tagName ) throws Exception {
        given().a_report_model()
            .and().scenario_$_has_tag_$_with_value_$( 1, "testtag", "#42" )
            .and().the_tag_has_prependType_set_to( prependType );
        jsonReports
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_All_Scenarios_page_is_opened()
            .and().the_tag_with_name_$_is_clicked( tagName );

        then().the_page_title_is( tagName );
    }

    @Test
    @FeatureTagsWithCustomStyle
    public void tags_with_custom_styles_are_shown_correctly() throws Exception {
        String style = "background-color: black;";
        given().a_report_model()
            .and().the_first_scenario_has_tag( "TagWithCustomStyle" )
            .and().the_tag_has_style( style );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_All_Scenarios_page_is_opened();

        then().the_page_contains_tag( "TagWithCustomStyle" )
            .and().the_tag_has_style( style );
    }

    @Test
    @Issue( "#191" )
    @FeatureAttachments
    public void attachments_of_all_cases_appear_in_the_HTML5_report_when_having_a_data_table() throws Exception {
        String content1 = "Some Example Attachment\nwith some example content";
        String content2 = "A second Example Attachment\nwith some example content";

        given().a_report_model()
            .and().the_scenario_has_one_parameter()
            .and().the_scenario_has_$_default_cases( 2 )
            .and().step_$_of_case_$_has_a_text_attachment( 1, 1 )
            .and().step_$_of_case_$_has_a_text_attachment( 1, 2 );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_page_of_scenario_$_is_opened( 1 );

        then().$_attachment_icons_exist( 2 );
    }

    @Test
    @FeatureAttachments
    public void attachments_appear_in_the_HTML5_report() throws Exception {
        String content = "Some Example Attachment\nwith some example content";
        given().a_report_model()
            .and().step_$_of_scenario_$_has_a_text_attachment_with_content( 1, 1, content );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_page_of_scenario_$_is_opened( 1 );

        then().an_attachment_icon_exists()
            .and().the_content_of_the_attachment_referenced_by_the_icon_is( content );
    }

    @Test
    @FeatureAttachments
    public void steps_can_have_multiple_attachments() throws Exception {
        String content1 = "Some Example Attachment\nwith some example content";
        String content2 = "Another Example Attachment\nwith some example content";
        given().a_report_model()
            .and().step_$_of_scenario_$_has_a_text_attachment_with_content( 1, 1, content1 )
            .and().step_$_of_scenario_$_has_another_text_attachment_with_content( 1, 1, content2 );
        jsonReports
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML_Report_Generator_is_executed();

        when().the_page_of_scenario_$_is_opened( 1 );

        then().$_attachment_icons_exist( 2 )
            .and().the_content_of_the_attachment_referenced_by_icon_$_is( 1, content1 )
            .and().the_content_of_the_attachment_referenced_by_icon_$_is( 2, content2 );
    }

    @Test
    public void the_configured_title_appears_in_the_generated_HTML_report() throws Exception {
        given().a_report_model();
        jsonReports
            .and().the_report_exist_as_JSON_file();

        whenReport
            .and().the_HTML_Report_Generator_is_executed_with_title( "Test Title" );

        when().the_index_page_is_opened();

        then().the_report_title_is( "Test Title" );
    }

    @Issue( "#146" )
    @Test
    @DataProvider( {
        "JGiven Documentation, http://jgiven.org/docs",
        "Back, javascript:window.history.back()" } )
    public void navigation_links_of_the_HTML_report_can_be_customized_using_a_custom_JS_file( String title, String href ) throws Exception {
        given().a_report_model();
        jsonReports
            .and().the_report_exist_as_JSON_file()
            .given().a_custom_JS_file_with_content(
                "jgivenReport.addNavigationLink( { \n"
                        + "   href: '" + href + "', \n"
                        + "   text: '" + title + "', \n"
                        + "   target: '_blank' \n"
                        + "});" );
        whenReport.when().the_HTML_Report_Generator_is_executed();

        when().and().the_index_page_is_opened();

        then().the_navigation_menu_has_a_link_with_text( title.toUpperCase() )
            .and().href( href )
            .and().target( "_blank" );
    }

    @Test
    @Issue( "#226" )
    public void newlines_are_detected_in_formatted_values_and_shown_as_multiline_text() throws IOException {
        String content = "Some \n text \n with \n newlines";
        given().a_report_model()
            .and().step_$_of_case_$_has_a_formatted_value_$_as_parameter( 1, 1, content );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport.when().the_HTML_Report_Generator_is_executed();
        when().the_page_of_scenario_$_is_opened( 1 );
        then().an_element_with_a_$_class_exists( "multiline" )
            .and().has_content( content );

    }

    @DataProvider
    public static Object[][] parserTestData() {
        return new Object[][] {
            { "Placeholder with index", "$1", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
            { "Placeholder without index", "$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "1" },
            { "Escaped placeholder", "$$", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "$" },
            { "Multiple placeholders with switch order", "$2 + $1", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ), "2 + 1" },
            { "Placeholders with additional text", "a = $1 and b = $2", Arrays.asList( "a", "b" ), Arrays.asList( 1, 2 ),
                "a = 1 and b = 2" },
            { "Placeholders references by argument names in order", "int = $int and str = $str and bool = $bool",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "int = 1 and str = some string and bool = true" },
            { "Placeholders references by argument names in mixed order", "str = $str and int = $int and bool = $bool",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "str = some string and int = 1 and bool = true" },
            { "Placeholders references by argument names and enumeration", "str = $str and int = $1 and bool = $bool",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "str = some string and int = 1 and bool = true" },
            { "Placeholders references by argument names and enumerations ", "bool = $3 and str = $2 and int = $int",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "bool = true and str = some string and int = 1" },
            { "Placeholder without index mixed with names", "bool = $bool and int = $ and str = $",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "bool = true and int = 1 and str = some string" },
            { "Placeholder without index mixed with names and index", "bool = $bool and str = $2 and int = $ and str = $ and bool = $3",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "bool = true and str = some string and int = 1 and str = some string and bool = true" },
            { "Placeholder with unknown argument names get erased", "bool = $bool and not known = $unknown and unknown = $10",
                Arrays.asList( "int", "str", "bool" ), Arrays.asList( 1, "some string", true ),
                "bool = true and not known = 1 and unknown = some string" },
            { "Non-Java-Identifier char does trigger a space after a placeholder", "$]",
                Collections.singletonList( "int" ), Collections.singletonList( 1 ), "1 ]" },
        };
    }

    @Test
    @Issue( "#236" )
    @UseDataProvider( "parserTestData" )
    @CaseAs( value = "$1" )
    public void extended_description_should_handle_every_case_correctly( String description, String value, List<String> parameterNames,
            List<Object> parameterValues,
            String expectedValue ) throws IOException {

        Map<String, String> argumentMap = new LinkedHashMap<>();
        for( int i = 0; i < parameterNames.size(); ++i ) {
            String argName = parameterNames.get( i );
            String argValue = String.valueOf( parameterValues.get( i ) );
            argumentMap.put( argName, argValue );
        }

        given().a_report_model()
            .and().step_$_of_scenario_$_has_extended_description_with_arguments( 1, 1, value, argumentMap );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport.when().the_HTML_Report_Generator_is_executed();
        when().the_page_of_scenario_$_is_opened( 1 )
            .and().show_tooltip_of_extended_description();
        then().an_element_with_a_$_class_exists( "has-tip" )
            .and().attribute_$_has_value_$( "tooltip-html-unsafe", expectedValue );
    }

    @Test
    @Issue( "#274" )
    public void a_thumbnail_is_shown_for_image_attachments() throws IOException {

        String screenshot = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );

        given().a_report_model()
            .and().step_$_of_scenario_$_has_an_image_attachment_$( 1, 1, screenshot );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport.when().the_HTML_Report_Generator_is_executed();
        when().the_page_of_scenario_$_is_opened( 1 );
        then().an_element_with_a_$_class_exists( "jgiven-html-thumbnail" )
            .and().the_image_is_loaded();
    }

    @Test
    @Issue( "#274" )
    @DataProvider( {
        "true",
        "false" } )
    public void showing_thumbnails_can_be_configured( boolean thumbOption ) throws IOException {
        String screenshot = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );

        given().a_report_model()
            .and().step_$_of_scenario_$_has_an_image_attachment_$( 1, 1, screenshot );
        jsonReports
            .and().the_report_exist_as_JSON_file();
        whenReport.when().showing_thumbnails_is_set_to( thumbOption )
            .and().the_HTML_Report_Generator_is_executed();
        when().the_page_of_scenario_$_is_opened( 1 );
        if( thumbOption ) {
            then().an_element_with_a_$_class_exists( "jgiven-html-thumbnail" )
                .and().the_image_is_loaded();
        } else {
            then().$_attachment_icons_exist( 1 );
        }
    }
}
