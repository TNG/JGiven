package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.openqa.selenium.*;

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

    protected WebElement findTagWithName( String tagName ) {
        List<WebElement> links = webDriver.findElements(
            By.xpath( String.format( "//a/span[contains(@class,'tag') and contains(text(), '%s')]/..", tagName ) ) );
        assertThat( links ).isNotEmpty();
        return links.get( 0 );
    }

}
