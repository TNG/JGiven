package com.tngtech.jgiven.examples.userguide;

//tag::noPackage[]
import com.tngtech.jgiven.Stage;

public class WhenSomeAction extends Stage<WhenSomeAction> {
    public WhenSomeAction some_action() {
        return self();
    }
}
//end::noPackage[]
