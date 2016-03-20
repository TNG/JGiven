package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assume;
import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.junit.ScenarioExecutionTest.TestStage;

public abstract class AbstractBridgeMethodTest<T extends AbstractBridgeMethodTest.BridgeMethodTestStage> extends
        ScenarioTest<T, TestStage, TestStage> {

    @Test
    public void bridge_methods_are_correctly_handled() {
        given().method_that_is_overidden_with_different_return_type();

        assertThat( getScenario().getScenarioCaseModel().getSteps() ).isNotEmpty();
    }

    @Test
    public void annotations_of_bridge_methods_are_correctly_read() {
        String version = System.getProperty( "java.version" );
        Assume.assumeFalse( "This works only since Java 8", version.startsWith( "1.6" ) || version.startsWith( "1.7" ) );

        given().method_with_formatter_$( "foo" );

        assertThat( getScenario().getScenarioCaseModel().getSteps().get( 0 ).getWord( 2 ).getFormattedValue() ).isEqualTo( "\"foo\"" );
    }

    static abstract class BridgeMethodTestStage<SELF extends BridgeMethodTestStage<?>> extends Stage<SELF> {
        BridgeMethodTestStage method_that_is_overidden_with_different_return_type() {
            return this;
        }

        abstract SELF method_with_formatter_$( @Quoted String text );

    }

}
