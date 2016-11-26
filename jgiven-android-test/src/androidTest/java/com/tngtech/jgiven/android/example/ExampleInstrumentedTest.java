package com.tngtech.jgiven.android.example;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.android.AndroidJGivenTestRule;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest  extends
        SimpleScenarioTest<ExampleInstrumentedTest.Steps> {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public AndroidJGivenTestRule androidJGivenTestRule = new AndroidJGivenTestRule(this.getScenario());

    @Test
    public void clicking_ClickMe_changes_the_text() {
        given().the_main_activity_is_shown()
           .and().text_$_is_shown("Hello World!");
        when().clicking_the_ClickMe_button();
        then().text_$_is_shown("JGiven Works!");

    }

    public static class Steps extends Stage<Steps> {
        public Steps the_main_activity_is_shown() {
            // nothing to do, just for reporting
            return this;
        }
        public Steps clicking_the_ClickMe_button() {
            onView(withId(R.id.clickMeButton)).perform(click());
            return this;
        }
        public Steps text_$_is_shown(@Quoted String s) {
            onView(withId(R.id.hellowordtext)).check(matches(withText(s)));
            return this;
        }
    }

}
