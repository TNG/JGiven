package com.tngtech.jgiven.spock.stages

import com.tngtech.jgiven.Stage

class Then extends Stage<Then> {

    Then some_outcome() {
        assert true
        self()
    }
}