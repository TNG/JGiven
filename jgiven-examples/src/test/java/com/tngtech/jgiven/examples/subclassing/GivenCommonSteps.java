package com.tngtech.jgiven.examples.subclassing;

import com.tngtech.jgiven.Stage;
//tag::noPackage[]
public class GivenCommonSteps extends Stage<GivenCommonSteps> {
    public GivenCommonSteps my_common_step() {
        return this;
    }
  //end::noPackage[]
    public GivenCommonSteps cant_do_this() {
        return this;        
    }
}
