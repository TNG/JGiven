package com.tngtech.jgiven.junit.tags;

@TestTag
public class ClassImplementingAbstractScenario
        extends
        AbstractScenarioForTestingTagInheritance<ClassImplementingAbstractScenario.GivenStep<?>, ClassImplementingAbstractScenario.GivenStep<?>, ClassImplementingAbstractScenario.GivenStep<?>> {

    public static class GivenStep<T> {}
}
