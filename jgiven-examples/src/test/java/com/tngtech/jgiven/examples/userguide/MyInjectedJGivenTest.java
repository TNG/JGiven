package com.tngtech.jgiven.examples.userguide;

import org.junit.Test;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.ScenarioTest;
//tag::noPackage[]
public class MyInjectedJGivenTest extends
   ScenarioTest<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {

   @ScenarioStage
   GivenAdditionalState additionalState;

   @Test
   public void something_should_happen() {
      given().some_state();

      additionalState
         .and().some_additional_state();

      when().some_action();
      then().some_outcome();
   }
}
//end::noPackage[]
