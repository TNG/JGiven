package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.Stage;
//tag::noPackage[]
public class WhenSomeAction extends Stage<WhenSomeAction> {
    public WhenSomeAction some_action() {
        //code for executing test goes here
       return self();
    }
 }
//end::noPackage[]
