package com.tngtech.jgiven.examples.userguide;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tngtech.jgiven.examples.pancakes.app.SpringConfig;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = SpringConfig.class )
public class RocketTest extends SimpleScenarioTest<RocketMethods> {    

    @Test
    public void First_Test_of_new_Rocket() {
        given().prepareRocketSimulator();
        when().launch_rocket();
        then().rocket_is_launched();
    }

}
