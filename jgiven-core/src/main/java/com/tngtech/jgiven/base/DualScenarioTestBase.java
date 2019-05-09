package com.tngtech.jgiven.base;

import com.google.common.reflect.TypeToken;
import com.tngtech.jgiven.impl.Scenario;


/**
 * ScenarioTest that only takes two type parameters, where the first is used for combined
 * GIVEN and WHEN and the second is used for THEN.
 *
 * This is useful for tests, where you often need to use WHEN steps also in GIVEN statements.
 *
 * This class is typically not directly used by end users,
 * but instead test-framework-specific classes for JUnit or TestNG
 */
public abstract class DualScenarioTestBase<GIVEN_WHEN, THEN> extends ScenarioTestBase<GIVEN_WHEN,GIVEN_WHEN, THEN> {

  @SuppressWarnings( { "serial", "unchecked" } )
  protected Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN> createScenario() {
    Class<GIVEN_WHEN> givenWhenClass = (Class<GIVEN_WHEN>) new TypeToken<GIVEN_WHEN>( getClass() ) {}.getRawType();
    Class<THEN> thenClass = (Class<THEN>) new TypeToken<THEN>( getClass() ) {}.getRawType();

    return new Scenario<GIVEN_WHEN, GIVEN_WHEN, THEN>( givenWhenClass, givenWhenClass, thenClass );
  }

}
