package com.tngtech.jgiven.playground;

import com.tngtech.jgiven.Stage;

public abstract class AbstractGiven<SELF extends AbstractGiven<SELF>> extends Stage<SELF> {
    public abstract SELF I_can_only_be_supplied_by_a_subclass();
}
