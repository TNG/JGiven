package com.tngtech.jgiven.report.text;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.report.model.GivenReportModel;
import com.tngtech.jgiven.tags.FeatureDataTables;
import com.tngtech.jgiven.tags.FeatureTextReport;

@FeatureDataTables
@FeatureTextReport
public class DataTablePlainTextScenarioWriterTest extends ScenarioTest<GivenReportModel<?>, WhenPlainTextWriter, ThenPlainTextOutput> {

    @Test
    public void data_tables_are_generated_in_text_reports() throws UnsupportedEncodingException {
        given()
            .a_report_model_with_one_scenario()
            .and().the_scenario_is_annotated_with_CasesAsTables()
            .and().the_scenario_has_parameters( "param1", "param2" )
            .and().the_scenario_has_$_cases( 3 )
            .and().case_$_has_arguments( 1, "arg10", "arg11" )
            .and().case_$_has_arguments( 2, "arg20", "arg21" )
            .and().case_$_has_arguments( 3, "arg30", "arg31" );

        when()
            .the_plain_text_report_is_generated();

        then()
            .the_plain_report_contains_the_text( "\n" +
                    "    | param1 | param2 |\n" +
                    "    +--------+--------+\n" +
                    "    |  arg10 |  arg11 |\n" +
                    "    |  arg20 |  arg21 |\n" +
                    "    |  arg30 |  arg31 |\n" );
    }
}
