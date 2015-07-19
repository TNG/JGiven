package com.tngtech.jgiven.report.html5;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;

public class Html5AppStage<SELF extends Html5AppStage<?>> extends Stage<SELF> {
    @ExpectedScenarioState
    protected CurrentStep currentStep;

    @ExpectedScenarioState
    protected WebDriver webDriver;

    @ExpectedScenarioState
    protected File targetReportDir;

    protected void takeScreenshot() {
        String base64 = ( (TakesScreenshot) webDriver ).getScreenshotAs( OutputType.BASE64 );
        currentStep.addAttachment( Attachment.fromBase64( base64, MediaType.PNG ).withTitle( "Screenshot" ) );
    }

}
