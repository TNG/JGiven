package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenReportExtractingExtension.class)
public class GuaranteedFieldRealTest extends
    ScenarioTest<GuaranteedFieldRealTest.RealGiven, GuaranteedFieldRealTest.RealWhen, GuaranteedFieldRealTest.RealThen> {

    @Test
    @org.junit.jupiter.api.Test
    public void a_sample_test() {
        given().a_sample_uninitialized_stage();
        when().I_do_something();
        then().I_did_something();
    }

    @Test
    @org.junit.jupiter.api.Test
    public void a_sample_initialized_test() {
        given().a_sample_initialized_stage();
        when().I_do_something();
        then().I_did_something();
    }

    public static class RealGiven extends Stage<RealGiven> {
        @ProvidedScenarioState(guaranteed = true)
        Object guaranteedObject = null;

        public void a_sample_uninitialized_stage() {
        }

        public void a_sample_initialized_stage() {
            this.guaranteedObject = "I'm initialized";
        }
    }

    public static class RealThen extends Stage<RealGiven> {
        public void I_did_something() {
        }
    }

    public static class RealWhen extends Stage<RealGiven> {
        @BeforeStage
        public void beforeSetup() throws ClassNotFoundException {
            throw new ClassNotFoundException("Not a JGiven exception");
        }

        public void I_do_something() {
        }
    }
}
