package com.tngtech.jgiven.scala.example

import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers._

class Steps {

  var someInt = 0

  def some_another_state(): Unit = {
    someInt = 5
  }

  def some_action(): Unit = {
    someInt *= 2
  }

  def some_outcome: Assertion = {
    someInt should be(10)
  }

}
