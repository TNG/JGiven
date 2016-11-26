package com.tngtech.jgiven.junit.de;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.junit.JGivenClassRule;
import com.tngtech.jgiven.lang.de.SzenarioTestBasis;

public class SzenarioTest<GEGEBEN, WENN, DANN> extends SzenarioTestBasis<GEGEBEN, WENN, DANN> {
    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

    @Override
    public Scenario<GEGEBEN, WENN, DANN> getScenario() {
        return (Scenario<GEGEBEN, WENN, DANN>) scenarioRule.getScenario();
    }
}
