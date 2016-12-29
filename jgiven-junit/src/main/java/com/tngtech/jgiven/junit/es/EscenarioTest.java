package com.tngtech.jgiven.junit.es;

import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

import com.tngtech.jgiven.junit.JGivenMethodRule;
import com.tngtech.jgiven.junit.JGivenClassRule;
import com.tngtech.jgiven.lang.es.EscenarioTestBase;

public class EscenarioTest<DADO, CUANDO, ENTONCES> extends EscenarioTestBase<DADO, CUANDO, ENTONCES> {
	@ClassRule
	public static final JGivenClassRule writerRule = new JGivenClassRule();

	@Rule
	public final JGivenMethodRule scenarioRule = new JGivenMethodRule(createScenario());

	@Override
	public Scenario<DADO, CUANDO, ENTONCES> getScenario() {
		return (Scenario<DADO, CUANDO, ENTONCES>) scenarioRule.getScenario();
	}
}
