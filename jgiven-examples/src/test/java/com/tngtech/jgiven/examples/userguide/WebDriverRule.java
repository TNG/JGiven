package com.tngtech.jgiven.examples.userguide;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

//tag::noPackage[]
public class WebDriverRule {

    protected WebDriver webDriver;

    public void before() {
         webDriver = new HtmlUnitDriver( true );
    }

    public void after() {
         webDriver.close();
    }
 }

//end::noPackage[]