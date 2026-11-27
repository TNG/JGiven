package com.tngtech.jgiven.impl;

import static com.tngtech.jgiven.impl.ConfigurationTest.TestAsProvider.CLASS_AS_RESULT;
import static com.tngtech.jgiven.impl.ConfigurationTest.TestAsProvider.METHOD_AS_RESULT;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.ScenarioTestBaseForTesting;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.AsProvider;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.Test;

@JGivenConfiguration( ConfigurationTest.TestConfiguration.class )
public class ConfigurationTest extends ScenarioTestBaseForTesting<ConfigurationTest.FooStage, WhenTestStep, ThenTestStep> {

    @Test
    public void testAsProviderConfiguration() throws Throwable{
        var testModel = new ReportModel();
        testModel.setTestClass(getClass());
        getScenario().setModel(testModel);
        getScenario().startScenario( this.getClass(), ConfigurationTest.class.getMethod( "testAsProviderConfiguration" ), List.of());
        given().default_formatted_step( new FooParam() );
        given().custom_formatted_step( new FooParam() );

        given().default_formatted_step( new FooParam() );
        given().custom_formatted_step( new FooParam() );

        getScenario().finished();
        assertThat(testModel.getName()).isEqualTo(CLASS_AS_RESULT);
        assertThat(getScenario().getScenarioCaseModel().getFirstStep().getName()).isEqualTo(METHOD_AS_RESULT);
        assertThat(getScenario().getScenarioModel().getDescription()).isEqualTo("Test method");
    }

    @Test
    public void testFormatterConfiguration() throws Throwable {
        getScenario().setModel( new ReportModel() );
        getScenario().startScenario( this.getClass(), ConfigurationTest.class.getMethod( "testFormatterConfiguration" ),List.of());
        given().default_formatted_step( new FooParam() );
        given().custom_formatted_step( new FooParam() );

        getScenario().finished();
        ScenarioModel model = getScenario().getScenarioModel();
        ScenarioCaseModel caseModel = model.getScenarioCases().get( 0 );
        String value = caseModel.getFirstStep().getLastWord().getFormattedValue();
        assertThat( value ).isEqualTo( "foo bar" );

        value = caseModel.getStep( 1 ).getLastWord().getFormattedValue();
        assertThat( value ).isEqualTo( "baz" );
    }

    static class FooStage {
        void default_formatted_step(FooParam fooBar ) {}

        void custom_formatted_step(@Format( FooParamFormatter2.class ) FooParam fooBar ) {}
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
            setAsProvider(new TestAsProvider());
        }
    }

    public static class TestAsProvider implements AsProvider {

        public static final String METHOD_AS_RESULT = "testMethod";
        public static final String CLASS_AS_RESULT = "testClass";
        @Override
        public String as(As annotation, Method method) {
            return METHOD_AS_RESULT;
        }

        @Override
        public String as(As annotation, Class<?> scenarioClass) {
            return CLASS_AS_RESULT;
        }
    }
}
