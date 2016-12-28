package com.tngtech.jgiven.lang.es;

import com.tngtech.jgiven.annotation.IntroWord;
import com.tngtech.jgiven.annotation.StepComment;
import com.tngtech.jgiven.base.StageBase;

/**
 * Una versión en español de la clase {@link com.tngtech.jgiven.Stage}.
 */
public class Fase<SELF extends Fase<?>> extends StageBase<SELF> {

    @IntroWord
    public SELF dado() {
        return self();
    }

    @IntroWord
    public SELF dada() {
        return self();
    }

    @IntroWord
    public SELF cuando() {
        return self();
    }

    @IntroWord
    public SELF entonces() {
        return self();
    }

    @IntroWord
    public SELF y() {
        return self();
    }

    @IntroWord
    public SELF pero() {
        return self();
    }

    @IntroWord
    public SELF con() {
        return self();
    }

    @StepComment
    public SELF comentario( String comentario ) {
        return self();
    }
}
