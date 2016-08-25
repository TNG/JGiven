package com.tngtech.jgiven.examples.webdriver;

//tag::noPackage[]
public class WebDriverRule {

    protected WebDriver webDriver;

    public void before() {
         webDriver = new WebDriver();
    }

    public void after() {
         webDriver.close();
    }
 }
//end::noPackage[]