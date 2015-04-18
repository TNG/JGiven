package com.tngtech.jgiven.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BasicStepdefs {

    @Given( "^some step$" )
    public void some_step() throws Throwable {}

    @When( "^I run a failing step" )
    public void I_run_a_failing_step() throws Throwable {
        // throw new RuntimeException( "production failed here" );
    }

    @Then( "^an exception will be thrown$" )
    public void an_exception_will_be_thrown() throws Throwable {}

}
