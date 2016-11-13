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
    public AndroidJGivenTestRule androidJGivenTestRule =new AndroidJGivenTestRule(this);

    @Before
    public  void setUp() throws Exception {
        mActivityTestRule.launchActivity( null);

    }

    @Test
    public void something_should_happen() {

        given().some_state();
        when().some_action();
        then().some_outcome();
    }

    public class GivenSomeState extends Stage<GivenSomeState> {

        public GivenSomeState some_state() {
            return self();
        }
    }


    public class WhenSomeAction extends Stage<WhenSomeAction> {
        public WhenSomeAction some_action() {
            return self();
        }
    }

    public class ThenSomeOutcome extends Stage<ThenSomeOutcome> {

        public ThenSomeOutcome some_outcome() {
            return self();
        }
    }

}
