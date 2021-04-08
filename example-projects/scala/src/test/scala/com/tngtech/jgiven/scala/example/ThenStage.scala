package com.tngtech.jgiven.scala.example

import com.tngtech.jgiven._
import com.tngtech.jgiven.annotation._
import org.scalatest.Assertions._

class ThenStage extends Stage[ThenStage] {

  @ExpectedScenarioState
  var result = 0

  def the_result_should_be(expectedResult: Int): ThenStage = {
    assert(result == expectedResult)
    this
  }

}
