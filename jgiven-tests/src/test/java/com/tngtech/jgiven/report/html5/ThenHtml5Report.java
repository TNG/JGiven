package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.StepAccess;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.attachment.BinaryAttachment;

public class ThenHtml5Report<SELF extends ThenHtml5Report<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    StepAccess stepAccess;

    @ExpectedScenarioState
    protected WebDriver webDriver;

    public SELF the_page_title_is( String pageTitle ) {
        assertThat( webDriver.findElement( By.id( "page-title" ) ).getText() ).isEqualTo( pageTitle );
        return self();
    }

    public SELF the_page_statistics_line_contains_text( String text ) throws IOException {
        assertThat( webDriver.findElement( By.className( "page-statistics" ) ).getText() ).contains( text );
        String base64 = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );
        stepAccess.addAttachment( BinaryAttachment.fromBase64PngImage( base64 ) );
        return self();
    }
}
