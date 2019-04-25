package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.annotation.Hidden;

public class RocketMethods {

    private RocketSimulator rocketSimulator;
    private boolean rocketLaunched;
    private String rocketName;
    private String rocketDescription;

    // tag::rocketSetup[]
    public void setup_rocket(@Hidden String rocketName, @Hidden String rocketDescription) {
        this.rocketName = rocketName;
        this.rocketDescription = rocketDescription;
    }

    // end::rocketSetup[]
    public void rocket_is_setup() {
        assert (!rocketName.isEmpty() && !rocketDescription.isEmpty());
    }

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
        assert (rocketLaunched);
    }

}
