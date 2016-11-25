package com.tngtech.jgiven.android.example;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.android.AndroidJGivenTestRule;
import com.tngtech.jgiven.junit.ScenarioTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Instrumentation example, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest  extends
        ScenarioTest<ExampleInstrumentedTest.GivenSomeState, ExampleInstrumentedTest.WhenSomeAction, ExampleInstrumentedTest.ThenSomeOutcome> {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Rule
    public AndroidJGivenTestRule androidJGivenTestRule =new AndroidJGivenTestRule(this.getScenario());



    @Test
    public void something_should_happen() {
        given().some_state();
        when().some_action();
        then().some_outcome();
        onView(withId(R.id.hellowordtext)).check(matches(isDisplayed()));

    }

    public static class GivenSomeState extends Stage<GivenSomeState> {

        public GivenSomeState some_state() {
            return this;
        }
    }


    public static class WhenSomeAction extends Stage<WhenSomeAction> {
        public WhenSomeAction some_action() {
            return this;
        }
    }

    public static class ThenSomeOutcome extends Stage<ThenSomeOutcome> {

        public ThenSomeOutcome some_outcome() {

            return this;
        }
    }

}
