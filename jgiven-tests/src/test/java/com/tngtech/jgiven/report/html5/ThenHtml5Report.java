package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

public class ThenHtml5Report<SELF extends ThenHtml5Report<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected WebDriver webDriver;

    public SELF the_page_title_is( String pageTitle ) {
        assertThat( webDriver.findElement( By.id( "page-title" ) ).getText() ).isEqualTo( pageTitle );
        return self();
    }

    public SELF the_page_statistics_line_contains_text( String text ) {
        assertThat( webDriver.findElement( By.className( "page-statistics" ) ).getText() ).contains( text );
        return self();
    }
}
