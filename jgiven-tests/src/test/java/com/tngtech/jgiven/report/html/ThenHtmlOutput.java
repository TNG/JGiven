package com.tngtech.jgiven.report.html;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;

public class ThenHtmlOutput extends Stage<ThenHtmlOutput> {
    @ExpectedScenarioState
    ReportModel reportModel;

    @ExpectedScenarioState
    String html;

    public ThenHtmlOutput the_HTML_report_contains_a_data_table_with_header_values( String... headerValues ) {
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append( ".*<table class='data-table'>\\s*<tr>.*" );
        patternBuilder.append( "\\s*<th>#</th>\\s*" );
        for( String value : headerValues ) {
            patternBuilder.append( "\\s*<th>" + value + "</th>\\s*" );
        }
        patternBuilder.append( "\\s*<th>Status</th>\\s*" );
        patternBuilder.append( "\\s*</tr>.*</table>.*" );
        return the_HTML_report_contains_pattern( patternBuilder.toString() );
    }

    public ThenHtmlOutput line_$_of_the_data_table_has_arguments_$( int line, String... args ) {
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append( ".*<table class='data-table'>.*" );
        patternBuilder.append( getPatternForTableLine( line, Arrays.asList( args ) ) );
        patternBuilder.append( "\\s*</table>.*" );
        return the_HTML_report_contains_pattern( patternBuilder.toString() );
    }

    public ThenHtmlOutput the_data_table_has_one_line_for_the_arguments_of_each_case() {
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append( ".*<table class='data-table'>.*" );
        for( ScenarioCaseModel caseModel : reportModel.getLastScenarioModel().getScenarioCases() ) {
            patternBuilder.append( getPatternForTableLine(caseModel.getCaseNr(), caseModel.getExplicitArguments() ) );
        }
        patternBuilder.append( "\\s*</table>.*" );
        return the_HTML_report_contains_pattern( patternBuilder.toString() );
    }

    private StringBuilder getPatternForTableLine( int caseNr, List<String> explicitArguments ) {
        StringBuilder patternBuilder = new StringBuilder();
        patternBuilder.append( "\\s*<tr>" );
        patternBuilder.append( "\\s*<td>" + caseNr + "</td>\\s*" );
        for( String arg : explicitArguments ) {
            patternBuilder.append( "\\s*<td>" + arg + "</td>\\s*" );
        }
        patternBuilder.append( "\\s*<td>.*icon-ok.*</td>\\s*" );
        patternBuilder.append( "\\s*</tr>" );
        return patternBuilder;
    }

    public ThenHtmlOutput the_HTML_report_contains_pattern( String patternString ) {
        Pattern pattern = Pattern.compile( ".*" + patternString + ".*", Pattern.MULTILINE | Pattern.DOTALL );
        Assertions.assertThat( html ).matches( pattern );
        return self();
    }

    public ThenHtmlOutput the_HTML_report_contains_text( String text ) {
        Assertions.assertThat( html ).contains( text );
        return self();
    }
}
