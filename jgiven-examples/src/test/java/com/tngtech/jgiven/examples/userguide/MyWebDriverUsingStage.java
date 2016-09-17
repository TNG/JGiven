package com.tngtech.jgiven.examples.userguide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;

// tag::rule[]
public class MyWebDriverUsingStage {
    @ScenarioRule
    protected WebDriverRule webDriverRule = new WebDriverRule();
    // end::rule[]

    @ProvidedScenarioState
    protected WebDriver web_Driver;

    @BeforeScenario
    public void doStuff() {
        web_Driver = new HtmlUnitDriver( true );
    }

    // tag::start[]
    @ProvidedScenarioState
    protected WebDriver webDriver;

    @BeforeScenario
    public void startBrowser() {
        webDriver = new HtmlUnitDriver( true );
    }

    // end::start[]
    // tag::middle[]
    @ProvidedScenarioState
    protected WebDriver webdriver;

    @AfterScenario
    public void closeBrowser() {
        webdriver.close();
    }
// end::middle[]
}
