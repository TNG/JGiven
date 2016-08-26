package com.tngtech.jgiven.examples.subclassing;

//tag::noPackage[]
public class GivenSpecialSteps extends GivenCommonSteps {
    public GivenSpecialSteps my_special_step() {
        return this;
    }
}
//end::noPackage[]