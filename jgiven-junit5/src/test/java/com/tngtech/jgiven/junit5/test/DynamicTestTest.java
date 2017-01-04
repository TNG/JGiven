package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collection;

import static com.tngtech.jgiven.junit5.DynamicJGivenTest.dynamicJGivenTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@ExtendWith(JGivenExtension.class)
@RunWith( JUnitPlatform.class )
@DisplayName("Dynamic Tests")
public class DynamicTestTest {

    @TestFactory
    Collection<org.junit.jupiter.api.DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
                dynamicJGivenTest("1st dynamic test", (s) -> {
                    s.given(GivenStage.class)
                            .some_state();
                    s.when(WhenStage.class)
                            .some_action();

                }),
                dynamicJGivenTest("2nd dynamic test", (scenario) -> {
                    scenario.given(GivenStage.class)
                            .some_state()
                            .when().some_action()
                            .then().some_outcome();
                })
        );
    }

}
