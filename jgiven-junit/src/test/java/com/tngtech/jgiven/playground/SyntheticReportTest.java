package com.tngtech.jgiven.playground;

public class SyntheticReportTest extends SomeTestBase<SyntheticReportTest.ConcreteGiven> {

    public static class ConcreteGiven extends AbstractGiven<ConcreteGiven> {
        @Override
        public ConcreteGiven I_can_only_be_supplied_by_a_subclass() {
            return self();
        }
    }
}