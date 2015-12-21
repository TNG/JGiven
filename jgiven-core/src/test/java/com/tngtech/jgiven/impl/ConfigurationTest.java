package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Collections;

import org.junit.Test;

import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;

@JGivenConfiguration( ConfigurationTest.TestConfiguration.class )
public class ConfigurationTest extends ScenarioTestBase<ConfigurationTest.FooStage, WhenTestStep, ThenTestStep> {

    @Test
    public void testFormatterConfiguration() throws Throwable {
        getScenario().setModel( new ReportModel() );
        getScenario().startScenario( this.getClass(), ConfigurationTest.class.getMethod( "testFormatterConfiguration" ),
            Collections.<NamedArgument>emptyList() );
        given().some_step( new FooParam() );
        given().another_step( new FooParam() );

        getScenario().finished();
        ScenarioModel model = getScenario().getScenarioModel();
        ScenarioCaseModel caseModel = model.getScenarioCases().get( 0 );
        String value = caseModel.getFirstStep().getLastWord().getFormattedValue();
        assertThat( value ).isEqualTo( "foo bar" );

        value = caseModel.getStep( 1 ).getLastWord().getFormattedValue();
        assertThat( value ).isEqualTo( "baz" );
    }

    static class FooStage {
        void some_step( FooParam fooBar ) {}

        void another_step( @Format( FooParamFormatter2.class ) FooParam fooBar ) {}
    }

    static class FooParam {

    }

    static class FooParamFormatter2 implements ArgumentFormatter<FooParam> {

        @Override
        public String format( FooParam argumentToFormat, String... formatterArguments ) {
            return "baz";
        }
    }

    static class FooParamFormatter implements Formatter<FooParam> {

        @Override
        public String format( FooParam argumentToFormat, Annotation... annotations ) {
            return "foo bar";
        }
    }

    public static class TestConfiguration extends AbstractJGivenConfiguration {

        @Override
        public void configure() {
            setFormatter( FooParam.class, new FooParamFormatter() );
        }
    }
}
