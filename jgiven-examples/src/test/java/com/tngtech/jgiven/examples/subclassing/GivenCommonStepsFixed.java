package com.tngtech.jgiven.examples.subclassing;

import com.tngtech.jgiven.Stage;

//tag::noPackage[]
public class GivenCommonStepsFixed<SELF extends GivenCommonStepsFixed<SELF>> extends Stage<SELF> {
    public SELF my_common_step() {
        return self();
    }
}
//end::noPackage[]