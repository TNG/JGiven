package com.tngtech.jgiven.exampleprojects.java21.module;

import com.tngtech.jgiven.junit6.ScenarioTest;
import org.junit.jupiter.api.Test;

/**
 * Exercises JGiven injection from inside a JPMS named module, using a
 * package-private stage class and stages with package-private
 * {@code @ScenarioState} fields.
 *
 * <p>This test only passes when the declaring module
 * {@code opens com.tngtech.jgiven.exampleprojects.java21.module} so that
 * {@link com.tngtech.jgiven.impl.ByteBuddyStageClassCreator} can inject the
 * generated stage subclass into the same runtime package and
 * {@link com.tngtech.jgiven.impl.inject.ValueInjector} can reflectively access
 * the package-private {@code @ScenarioState} fields.</p>
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
