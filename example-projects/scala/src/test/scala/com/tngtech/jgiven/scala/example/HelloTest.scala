package com.tngtech.jgiven.scala.example

import com.tngtech.jgiven.junit5.ScenarioTest
import org.junit.jupiter.api.Test

class HelloTest extends ScenarioTest[GivenStage, WhenStage, ThenStage] {

  @Test
  def my_first_JGiven_scenario_in_Scala(): Unit = {
    given.some_value(5).and.another_value(6)
    when.adding_the_values()
    `then`.the_result_should_be(11)
  }

}
