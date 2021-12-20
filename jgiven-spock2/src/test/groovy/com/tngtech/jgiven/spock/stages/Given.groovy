package com.tngtech.jgiven.spock.stages

import com.tngtech.jgiven.Stage
import com.tngtech.jgiven.annotation.As

class Given extends Stage<Given> {

    Given some_state() {
        self()
    }

    def some_state_$(String param) {
        self()
    }
}


