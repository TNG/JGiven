package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;

public class RocketMethods {
    
    private RocketSimulator rocketSimulator;
    private boolean rocketLaunched;

    // tag::hiddenRocket[]
    @Hidden
    public void prepareRocketSimulator() {
        rocketSimulator = createRocketSimulator();
    }
// end::hiddenRocket[]
    private RocketSimulator createRocketSimulator() {
        return new RocketSimulator();
    }
    // tag::rocketDesc[]
    @ExtendedDescription("Actually uses a rocket simulator")
    public RocketMethods launch_rocket() {
        rocketLaunched = rocketSimulator.launchRocket();
        return this;
    }
// end::rocketDesc[]
    public void rocket_is_launched() {
        assert(rocketLaunched);
    }

}
