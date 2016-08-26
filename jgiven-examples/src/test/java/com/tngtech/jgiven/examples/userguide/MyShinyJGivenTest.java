package com.tngtech.jgiven.examples.userguide;

import org.junit.Test;
import com.tngtech.jgiven.junit.ScenarioTest;
//tag::noPackage[]
public class MyShinyJGivenTest extends ScenarioTest<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {
   @Test
   public void something_should_happen() {
      given().some_state();
      when().some_action();
      then().some_outcome();
   }
}
//end::noPackage[]
