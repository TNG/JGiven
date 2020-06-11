package com.tngtech.jgiven.examples.pancakes.test.stages;

import com.tngtech.jgiven.annotation.BackStep;
import com.tngtech.jgiven.annotation.RootStep;
import com.tngtech.jgiven.annotation.SyntacticSugar;
import com.tngtech.jgiven.impl.NavigableStage;

public abstract class MyNavigableStage<ROOT, BACK, SELF extends MyNavigableStage<ROOT, BACK, SELF>> extends NavigableStage<ROOT, BACK, SELF> {

    public static class Top<SELF extends Top<SELF>> extends MyNavigableStage<SELF, SELF, SELF> {

    }

    @RootStep
    public ROOT also() { return root(); }

    @BackStep
    public BACK and_a() { return back(); }

    @BackStep
    public BACK and_the() {
        return back();
    }

    @SyntacticSugar
    public SELF a() {
        return self();
    }

    @SyntacticSugar
    public SELF an() {
        return self();
    }

    @SyntacticSugar
    public SELF and() {
        return self();
    }

    @SyntacticSugar
    public SELF are() {
        return self();
    }

    @SyntacticSugar
    public SELF has() {
        return self();
    }

    @SyntacticSugar
    public SELF of() {
        return self();
    }

    @SyntacticSugar
    public SELF the() {
        return self();
    }

    @SyntacticSugar
    public SELF there() {
        return self();
    }

    @SyntacticSugar
    public SELF with() {
        return self();
    }

    @SyntacticSugar
    public SELF but() {
        return self();
    }

}
