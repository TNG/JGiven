package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;

public class WhenCalculator extends Stage<WhenCalculator> {
    // tag::method[]
    @As( "$ % are added" )
    public WhenCalculator $_percent_are_added( int percent ) {
        return self();
    }
    // end::method[]

}
