package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JGivenExtension.class)
class JUnit5NoScenarioTest {

    @ScenarioStage
    TestStage testStage;

    @Test
    void running_jgiven_without_scenario(){
        testStage.given().a_class_with_only_an_extension();
        testStage.when().i_run_the_test();
        testStage.then().i_get_a_report();

    }


    @SuppressWarnings("UnusedReturnValue")
    static class TestStage extends Stage<TestStage>{
       TestStage a_class_with_only_an_extension(){
          return self();
       }
       TestStage i_run_the_test(){
          return self();
       }

       TestStage i_get_a_report(){
           var myScenario = ScenarioHolder.get().getScenarioOfCurrentThread();
           assertThat(myScenario.getModel().getClassName()).isEqualTo(JUnit5NoScenarioTest.class.getName());
           return self();
       }
    }
}
