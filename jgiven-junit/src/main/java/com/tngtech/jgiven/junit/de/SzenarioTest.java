package com.tngtech.jgiven.junit.de;

import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioReportRule;
import com.tngtech.jgiven.lang.de.SzenarioTestBasis;

public class SzenarioTest<GEGEBEN, WENN, DANN> extends SzenarioTestBasis<GEGEBEN, WENN, DANN> {
    @ClassRule
    public static final ScenarioReportRule writerRule = new ScenarioReportRule();

    @Rule
    public final ScenarioExecutionRule scenarioRule = new ScenarioExecutionRule( writerRule, this, getScenario() );

}
