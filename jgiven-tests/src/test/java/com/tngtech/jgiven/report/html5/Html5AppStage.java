package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import java.io.File;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Html5AppStage<SELF extends Html5AppStage<?>> extends Stage<SELF> {
    private static final int WEBDRIVER_FIND_TIMEOUT_SECONDS = 120;
    @ExpectedScenarioState
    protected CurrentStep currentStep;

    @ExpectedScenarioState
    protected WebDriver webDriver;

    @ExpectedScenarioState
    protected File targetReportDir;

    protected void takeScreenshot() {
        String base64 = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BASE64);
        currentStep.addAttachment(Attachment.fromBase64(base64, MediaType.PNG).withTitle("Screenshot"));
    }

    protected WebElement findTagWithName(String tagName) {
        WebDriverWait timeoutSetter = new WebDriverWait(webDriver, WEBDRIVER_FIND_TIMEOUT_SECONDS);

        By elementLocator = By.xpath(String.format("//a/span[contains(@class,'tag') and contains(text(), '%s')]/..",
                tagName));
        timeoutSetter.until(ExpectedConditions.visibilityOfElementLocated(elementLocator));
        List<WebElement> links = webDriver.findElements(elementLocator);

        assertThat(links).isNotEmpty();
        return links.get(0);
    }

}
