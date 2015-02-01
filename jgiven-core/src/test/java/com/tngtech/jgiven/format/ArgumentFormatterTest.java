package com.tngtech.jgiven.format;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.report.text.PlainTextReporter;

public class ArgumentFormatterTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Format( value = BooleanFormatter.class, args = { "yes", "no" } )
    @Retention( RetentionPolicy.RUNTIME )
    @interface YesNo {}

    static class FormattedSteps {

        public void yesno_$_formatted( @YesNo boolean b ) {

        }
    }

    // @Test
    public void formatter_are_applied_to_arguments() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.yesno_$_formatted( true );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "yesno_yes_formatted" );
    }

}
