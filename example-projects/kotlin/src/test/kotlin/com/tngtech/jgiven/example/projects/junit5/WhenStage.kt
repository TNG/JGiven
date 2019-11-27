package com.tngtech.jgiven.example.projects.junit5

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.ScenarioState

open class WhenStage : Stage<WhenStage>() {

    @ScenarioState(required = true)
    lateinit var message: String

    @ScenarioState
    lateinit var result: String

    open fun handle_message() {
        result = "$message 5!"
    }
}
