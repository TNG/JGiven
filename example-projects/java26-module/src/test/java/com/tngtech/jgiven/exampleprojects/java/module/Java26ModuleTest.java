package com.tngtech.jgiven.exampleprojects.java.module;

import com.tngtech.jgiven.junit6.ScenarioTest;
import org.junit.jupiter.api.Test;

/**
 * Exercises JGiven injection from inside a JPMS named module using a
 * package-private stage class and package-private {@code @ScenarioState}
 * fields; see {@code module-info.java} for the required {@code opens}
 * directive.
 */
class Java26ModuleTest
    extends ScenarioTest<PackagePrivateStage, PackagePrivateStage, ThenStage> {

    @Test
    void jgiven_injects_package_private_stage_and_fields_in_a_named_module() {
        given().given_message("Hello");
        when().when_appending_suffix(" module!");
        then().the_result_is("Hello module!");
    }
}
