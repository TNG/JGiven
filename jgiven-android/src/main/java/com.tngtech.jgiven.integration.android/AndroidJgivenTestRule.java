package com.tngtech.jgiven.integration.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;

import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;

/**
 * Created by originx on 11/12/2016.
 */

public class AndroidJGivenTestRule extends UiThreadTestRule {
    private static final String TAG = "ActivityTestRule";

    public AndroidJGivenTestRule(ScenarioExecutionRule scenarioExecutionRule, Context context) {
        scenarioExecutionRule.getScenario().setExecutor(new AndroidScenarioExecutor(context));
    }
}
