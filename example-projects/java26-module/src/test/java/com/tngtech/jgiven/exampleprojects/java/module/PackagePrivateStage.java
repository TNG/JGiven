package com.tngtech.jgiven.exampleprojects.java.module;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioState;

/**
 * Package-private stage with package-private {@code @ScenarioState} fields,
 * exercising JGiven's ByteBuddy injection into the same runtime package.
 */
class PackagePrivateStage extends Stage<PackagePrivateStage> {

    @ScenarioState
    String message;

    @ScenarioState
    String result;

    PackagePrivateStage given_message(@Quoted String message) {
        this.message = message;
        return self();
    }

    PackagePrivateStage when_appending_suffix(String suffix) {
        result = message + suffix;
        return self();
    }
}
