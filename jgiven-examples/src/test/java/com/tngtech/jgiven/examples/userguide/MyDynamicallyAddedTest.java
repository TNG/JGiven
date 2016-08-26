package com.tngtech.jgiven.examples.userguide;

// tag::noPackage[]
import org.junit.Test;
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyDynamicallyAddedTest extends
   ScenarioTest<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {

   @Test
   public void something_should_happen() {
      GivenAdditionalState additionalState = addStage(GivenAdditionalState.class);

      given().some_state();

      additionalState
         .and().some_additional_state();

      when().some_action();
      then().some_outcome();
   }
}
// end::noPackage[]
