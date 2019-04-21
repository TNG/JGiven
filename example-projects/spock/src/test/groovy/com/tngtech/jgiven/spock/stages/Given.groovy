package com.tngtech.jgiven.spock.stages

import com.tngtech.jgiven.Stage

class GivenSomeState extends Stage<GivenSomeState> {

    GivenSomeState some_state() {
        self()
    }

    def some_state_$(String param) {
        self()
    }
}