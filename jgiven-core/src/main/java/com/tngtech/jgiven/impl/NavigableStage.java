package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.Stage;

public abstract class NavigableStage<ROOT, BACK, SELF extends NavigableStage<ROOT, BACK, SELF>> extends Stage<SELF> {

    private ROOT root;
    private BACK back;

    SELF root( ROOT root ) {
        this.root = root;
        return self();
    }

    protected ROOT root() {
        return root;
    }

    SELF back( BACK back ) {
        this.back = back;
        return self();
    }

    protected BACK back() {
        return back;
    }

}
