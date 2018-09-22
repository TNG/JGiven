package com.tngtech.jgiven.junit;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.test.GivenTestStep;

@RunWith( PowerMockRunner.class )
@PowerMockRunnerDelegate( BlockJUnit4ClassRunner.class )
public class PowerMockTest {
    @ClassRule
    public static final JGivenClassRule writerRule = new JGivenClassRule();

    @Rule
    public final JGivenMethodRule scenarioRule = new JGivenMethodRule();

    @ScenarioStage
    GivenTestStep someState;

    @Test
    public void JGiven_works_with_PowerMock() {
        someState.something();
    }

}
