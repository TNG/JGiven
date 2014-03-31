package com.tngtech.jgiven.lang.de;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.base.StageBase;

/**
 * A German version for step definitions
 */
public class Schritte<SELF extends Schritte<?>> extends StageBase<SELF> {

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

}
