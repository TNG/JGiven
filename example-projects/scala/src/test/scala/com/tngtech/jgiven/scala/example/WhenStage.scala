package com.tngtech.jgiven.scala.example

import com.tngtech.jgiven.annotation.{ExpectedScenarioState, ProvidedScenarioState}

class WhenStage {

  @ExpectedScenarioState
  var someInt = 0

  @ExpectedScenarioState
  var anotherInt = 0

  @ProvidedScenarioState
  var result = 0

  def adding_the_values(): WhenStage = {
    result = someInt + anotherInt
    this
  }
}
