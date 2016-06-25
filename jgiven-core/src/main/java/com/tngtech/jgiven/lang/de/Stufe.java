package com.tngtech.jgiven.lang.de;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.StepComment;
import com.tngtech.jgiven.base.StageBase;

/**
 * Eine deutsche Version der {@link com.tngtech.jgiven.Stage}-Klasse.
 */
public class Stufe<SELF extends Stufe<?>> extends StageBase<SELF> {

    @IntroWord
    public SELF angenommen() {
        return self();
    }

    @IntroWord
    public SELF gegeben() {
        return self();
    }

    @IntroWord
    public SELF wenn() {
        return self();
    }

    @IntroWord
    public SELF dann() {
        return self();
    }

    @IntroWord
    public SELF und() {
        return self();
    }

    @IntroWord
    public SELF aber() {
        return self();
    }

    @IntroWord
    public SELF mit() {
        return self();
    }

    @StepComment
    public SELF kommentiere( String kommentar ) {
        return self();
    }

}
