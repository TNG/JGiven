package com.tngtech.jgiven.example.projects.junit5

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioState
import org.junit.jupiter.api.Assertions

open class ThenStage : Stage<ThenStage>() {

    @ScenarioState(required = true)
    lateinit var result: String

    open fun the_result_is(expectedResult: String) {
        Assertions.assertEquals(expectedResult, result)
    }
}
