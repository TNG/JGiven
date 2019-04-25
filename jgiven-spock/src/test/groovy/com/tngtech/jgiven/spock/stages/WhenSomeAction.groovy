package com.tngtech.jgiven.spock.stages

import com.tngtech.jgiven.Stage

class WhenSomeAction extends Stage<WhenSomeAction> {

    WhenSomeAction some_action() {
        self()
    }
}