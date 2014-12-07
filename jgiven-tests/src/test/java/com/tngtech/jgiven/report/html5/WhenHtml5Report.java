package com.tngtech.jgiven.report.html5;

import java.io.File;
import java.net.MalformedURLException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class WhenHtml5Report<SELF extends WhenHtml5Report<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    protected WebDriver webDriver = new PhantomJSDriver();

    @ProvidedScenarioState
    protected File targetReportDir;

    public SELF the_index_page_is_opened() throws MalformedURLException {
        File file = new File( targetReportDir, "index.html" );
        webDriver.get( file.toURI().toURL().toString() );
        return self();
    }

    @AfterScenario
    protected void closeWebDriver() {
        // webDriver.close();
    }
}
