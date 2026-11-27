package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.junit6.ScenarioTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * A test class with only disabled test methods to reproduce #338
 */
public class AllDisabledTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    @Disabled
    void aTestThatIsDisabled() {
        given().some_state();
    }
}
