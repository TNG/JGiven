package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.StepComment;
import com.tngtech.jgiven.base.StageBase;

/**
 * Base class for Stages.
 * Defines some introduction words.
 * Follows the fluent interface pattern.
 *
 * @param <SELF> the type of the extending class to realize the fluent interface
 */
public class Stage<SELF extends Stage<?>> extends StageBase<SELF> {

    @IntroWord
    public SELF given() {
        return self();
    }

    @IntroWord
    public SELF when() {
        return self();
    }

    @IntroWord
    public SELF then() {
        return self();
    }

    @IntroWord
    public SELF and() {
        return self();
    }

    @IntroWord
    public SELF with() {
        return self();
    }

    @IntroWord
    public SELF but() {
        return self();
    }

    @StepComment
    public SELF comment( String comment ) {
        return self();
    }

}