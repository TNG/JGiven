package com.tngtech.jgiven.examples.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;

public class MyStage {
    //tag::rule[]
    @ScenarioRule
    protected WebDriverRule webDriverRule = new WebDriverRule();
    
    @ProvidedScenarioState
    protected WebDriver web_Driver;

    @BeforeScenario
    public void doStuff() {
        web_Driver = new HtmlUnitDriver( true );
    }
    //end::rule[]
    //tag::start[]
    @ProvidedScenarioState
    protected WebDriver webDriver;

    @BeforeScenario
    public void startBrowser() {
        webDriver = new HtmlUnitDriver( true );
    }
    //end::start[]
    //tag::middle[]
    @ProvidedScenarioState
    protected WebDriver webdriver;

    @AfterScenario
    public void closeBrowser() {
        webdriver.close();
    }
// end::middle[]
}
