package com.tngtech.jgiven.examples.subclassing;

import org.junit.Test;

import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class CommonStagesTest extends SimpleScenarioTest<GivenSpecialSteps> {

    // tag::onlyMethod[]
    @Test
    public void subclassing_of_stages_should_work() {
        given().my_common_step()
          .and().cant_do_this();
    }
  //end::onlyMethod[]
}
