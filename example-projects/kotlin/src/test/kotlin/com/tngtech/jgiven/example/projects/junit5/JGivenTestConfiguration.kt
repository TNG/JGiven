package com.tngtech.jgiven.example.projects.junit5

import org.junit.jupiter.api.Tag

import com.tngtech.jgiven.config.AbstractJGivenConfiguration

class JGivenTestConfiguration : AbstractJGivenConfiguration() {

    override fun configure() {
        configureTag(Tag::class.java)
            .prependType(true)
            .color("orange")
    }
}
