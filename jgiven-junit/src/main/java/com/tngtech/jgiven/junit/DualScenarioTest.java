package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.base.DualScenarioTestBase;
import com.tngtech.jgiven.impl.Scenario;
import org.junit.ClassRule;
import org.junit.Rule;

public class DualScenarioTest <GIVEN_WHEN, THEN> extends DualScenarioTestBase<GIVEN_WHEN, THEN> {

  @ClassRule
  public static final JGivenClassRule writerRule = new JGivenClassRule();

  @Rule
  public final JGivenMethodRule scenarioRule = new JGivenMethodRule( createScenario() );

  @Override
  @SuppressWarnings("unchecked")
  public Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> getScenario() {
    return (Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN>) scenarioRule.getScenario();
  }
}
