package com.tngtech.jgiven.cucumber;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith( Cucumber.class )
@CucumberOptions( format = { "pretty", "html:build/cucumber-html-report", "json:build/cucumber-report.json" } )
public class RunCukesTest {

}
