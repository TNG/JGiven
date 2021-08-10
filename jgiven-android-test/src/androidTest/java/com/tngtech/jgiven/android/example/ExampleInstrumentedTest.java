package com.tngtech.jgiven.android.example;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.integration.android.AndroidJGivenTestRule;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest  extends
        SimpleScenarioTest<ExampleInstrumentedTest.Steps> {
    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public AndroidJGivenTestRule androidJGivenTestRule = new AndroidJGivenTestRule(this.getScenario());

    @Test
    public void clicking_ClickMe_changes_the_text() {
        given().the_initial_main_activity_is_shown()
           .with().text("Hello World!");
        when().clicking_the_Click_Me_button();
        then().text_$_is_shown("JGiven Works!");

    }

    public static class Steps extends Stage<Steps> {
        @ScenarioState
        CurrentStep currentStep;

        @ScenarioState
        ActivityScenarioRule<MainActivity> activityTestRule;

        public Steps the_initial_main_activity_is_shown() {
            // nothing to do, just for reporting
            return this;
        }

        public Steps clicking_the_Click_Me_button() {
            onView(withId(R.id.clickMeButton)).perform(click());
            return this;
        }

        public Steps text(@Quoted String s) {
            return text_$_is_shown(s);
        }

        public Steps text_$_is_shown(@Quoted String s) {
            onView(withId(R.id.hellowordtext)).check(matches(withText(s)));
            InstrumentationRegistry.getInstrumentation()
                                .runOnMainSync(this::takeScreenshot);
            return this;
        }

        private void takeScreenshot() {
            List<Activity> resumedActivities =
                    (List<Activity>) ActivityLifecycleMonitorRegistry.getInstance()
                            .getActivitiesInStage(androidx.test.runner.lifecycle.Stage.RESUMED);
            Activity currentlyDisplayedActivity = resumedActivities.get(0);
            currentStep.addAttachment(
                    Attachment.fromBinaryBytes(ScreenshotUtil.takeScreenshot(currentlyDisplayedActivity), MediaType.PNG)
                        .showDirectly()) ;
        }

    }

}
