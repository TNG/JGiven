package com.tngtech.jgiven.integration.android;

import android.os.Environment;
import android.support.test.InstrumentationRegistry;

import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.junit.ScenarioTest;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;

public class AndroidJGivenTestRule implements TestRule {
    public AndroidJGivenTestRule(ScenarioBase scenario) {
        scenario.setExecutor(new AndroidScenarioExecutor(InstrumentationRegistry.getTargetContext()));

        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + InstrumentationRegistry.getTargetContext().getPackageName()
                        + " android.permission.WRITE_EXTERNAL_STORAGE");
        File reportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "jgiven-reports").getAbsoluteFile();
        Config.config().setReportDir(reportDir);
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
