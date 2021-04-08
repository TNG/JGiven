package com.tngtech.jgiven.scala.example

import com.tngtech.jgiven.junit5.SimpleScenarioTest
import org.junit.jupiter.api.Test

class SimpleHelloTest extends SimpleScenarioTest[Steps] {

  @Test
  def my_first_JGiven_scenario_in_Scala(): Unit = {
    given.some_another_state()
    when.some_action()
    `then`.some_outcome
  }

}
