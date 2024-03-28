package com.tngtech.jgiven.example.projects.junit5

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.Quoted
import com.tngtech.jgiven.annotation.ScenarioState

/**
 * In order for the @ScenarioState to be correctly injected the class and all public methods
 * must be declared open
 */
open class GivenStage : Stage<GivenStage>() {

    @ScenarioState
    lateinit var message: String

    open fun message(@Quoted message: String): GivenStage {
        this.message = message
        return self()
    }

    @JvmInline
    value class Username(val value: String)

    open fun greeting(username: Username): GivenStage {
        this.message = "Hello ${username.value}"
        return self()
    }
}
