package com.tngtech.jgiven.integration.android;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.os.Environment;
import androidx.test.platform.app.InstrumentationRegistry;
import com.tngtech.jgiven.impl.Config;
import com.tngtech.jgiven.impl.ScenarioBase;
import java.io.File;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class AndroidJGivenTestRule implements TestRule {
    public AndroidJGivenTestRule(ScenarioBase scenario) {
        scenario.setStageClassCreator(new AndroidStageClassCreator());

        grantPermission("READ_EXTERNAL_STORAGE");
        grantPermission("WRITE_EXTERNAL_STORAGE");

        File reportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "jgiven-reports").getAbsoluteFile();
        Config.config().setReportDir(reportDir);
    }

    private void grantPermission(String permission) {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand(
                "pm grant " + getApplicationContext().getPackageName()
                        + " android.permission." + permission);
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
