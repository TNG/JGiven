package com.tngtech.jgiven.integration.android;

import android.app.Activity;
import android.content.Context;

import com.tngtech.jgiven.impl.Scenario;
import com.tngtech.jgiven.junit.ScenarioTest;

/**
 * Base class for {@link AndroidScenarioExecutor} based JGiven tests
 *
 * @param <GIVEN>
 * @param <WHEN>
 * @param <THEN>
 * Created by originx on 10/17/2016.
 * @since 0.13.0
 */
public abstract class AndroidScenarioTest<GIVEN, WHEN, THEN> extends ScenarioTest<GIVEN, WHEN, THEN>  {
    public AndroidScenarioTest() {
    }

}

