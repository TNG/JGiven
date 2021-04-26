package com.tngtech.jgiven.example.projects.testng;

import com.tngtech.jgiven.testng.ScenarioTest;
import org.testng.annotations.Test;

class TestNgTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    public void scenario_with_TestNg(){
        given().message("Hello");
        when().handle_message();
        then().the_result_is("Hello TestNG!");
    }
}
