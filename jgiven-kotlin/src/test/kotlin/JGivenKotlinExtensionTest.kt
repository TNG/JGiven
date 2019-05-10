package com.tngtech.jgiven.kotlin

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ExpectedScenarioState
import com.tngtech.jgiven.annotation.ProvidedScenarioState
import com.tngtech.jgiven.annotation.ScenarioState.Resolution.NAME
import com.tngtech.jgiven.junit.ScenarioTest
import org.junit.Assert
import org.junit.Test

/**
 * Simple Test that just checks the sum of two numbers, displaying how the allopen plugin
 * works together with JGivenStage.
 *
 * given(), when(), then(), ...  are replaced by extension attributes for increased readability.
 */
class JGivenKotlinExtensionTest : ScenarioTest<JGivenKotlinGiven, JGivenKotlinWhen, JGivenKotlinThen>() {

  @Test
  fun `add two numbers`() {
    GIVEN
        .number(5)

    WHEN
        .we_add_number(7)

    THEN
        .the_result_is(12)
  }
}


@JGivenStage
class JGivenKotlinGiven : Stage<JGivenKotlinGiven>() {

  @ProvidedScenarioState private var firstNumber : Int = 0

  fun number(number : Int) : JGivenKotlinGiven = SELF.apply { firstNumber = number }
}

@JGivenStage
class JGivenKotlinWhen : Stage<JGivenKotlinWhen>() {

  @ExpectedScenarioState private var firstNumber : Int = 0
  @ProvidedScenarioState private var secondNumber : Int = 0

  fun we_add_number(number: Int) = SELF.apply { secondNumber = number }

}

@JGivenStage
class JGivenKotlinThen : Stage<JGivenKotlinThen>() {

  @ProvidedScenarioState(resolution = NAME) private var firstNumber : Int = 0
  @ProvidedScenarioState(resolution = NAME) private var secondNumber : Int = 0

  fun the_result_is(expected: Int) = SELF.apply {
    Assert.assertEquals(expected, firstNumber + secondNumber)
  }

}

