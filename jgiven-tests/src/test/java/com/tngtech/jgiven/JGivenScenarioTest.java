package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.junit.ScenarioTest;

@JGivenConfiguration( JGivenTestConfiguration.class )
public class JGivenScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTest<GIVEN, WHEN, THEN> {

}
