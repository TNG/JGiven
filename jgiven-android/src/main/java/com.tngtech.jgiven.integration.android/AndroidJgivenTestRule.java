package com.tngtech.jgiven.integration.android;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.UiThreadTestRule;

import com.tngtech.jgiven.impl.ScenarioExecutor;
import com.tngtech.jgiven.junit.ScenarioExecutionRule;
import com.tngtech.jgiven.junit.ScenarioTest;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by originx on 11/12/2016.
 */

public class AndroidJGivenTestRule implements TestRule {
    public AndroidJGivenTestRule(ScenarioTest scenarioTest) {
        scenarioTest.getScenario().setExecutor(new AndroidScenarioExecutor(InstrumentationRegistry.getTargetContext()));
        ScenarioTest.writerRule.getCommonReportHelper().setReportDir(InstrumentationRegistry.getTargetContext().getCacheDir().getAbsoluteFile());

    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }
}
