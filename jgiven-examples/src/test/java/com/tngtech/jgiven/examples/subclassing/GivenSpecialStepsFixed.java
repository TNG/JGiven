package com.tngtech.jgiven.examples.subclassing;

//tag::noPackage[]
public class GivenSpecialStepsFixed<SELF extends GivenSpecialStepsFixed<SELF>> extends GivenCommonStepsFixed<SELF> {
    public SELF my_special_step() {
        return self();
    }
}
//end::noPackage[]