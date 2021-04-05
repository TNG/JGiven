package com.tngtech.jgiven.scala.example

import com.tngtech.jgiven._
import com.tngtech.jgiven.annotation._

class GivenStage extends Stage[GivenStage] {

  @ProvidedScenarioState
  var someInt = 0

  @ProvidedScenarioState
  var anotherInt = 0

  def some_value(a: Int): GivenStage = {
    someInt = a
    this
  }

  def another_value(b: Int): GivenStage = {
    anotherInt = b
    this
  }

}
