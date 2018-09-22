package com.tngtech.jgiven.spock.stages

import com.tngtech.jgiven.Stage

class ThenSomeOutcome extends Stage<ThenSomeOutcome> {

    ThenSomeOutcome some_outcome() {
        assert true
        self()
    }
}