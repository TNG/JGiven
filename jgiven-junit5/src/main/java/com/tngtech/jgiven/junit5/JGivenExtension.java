package com.tngtech.jgiven.junit5;

import static com.tngtech.jgiven.report.model.ExecutionStatus.FAILED;
import static com.tngtech.jgiven.report.model.ExecutionStatus.SUCCESS;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ContainerExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.impl.ReportModelHolder;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.NamedArgument;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * This extension enables JGiven for JUnit 5 Tests.
 * <p>
 * Just annotate your test class with {@code @ExtendWith( JGivenExtension.class )}
 * <p>
 * You can then inject stage classes by using the {@see ScenarioStage} annotation
 * <p>
 * As an alternative you can also inherit from one of the following predefined base classes:
 * <ul>
 *     <li>{@link ScenarioTest}</li>
 *     <li>{@link SimpleScenarioTest}</li>
 * </ul>
 *
 * @see ScenarioTest
 * @see SimpleScenarioTest
 * @since 0.14.0
 */
public class JGivenExtension implements
        TestInstancePostProcessor,
        BeforeAllCallback,
        AfterAllCallback,
        BeforeEachCallback,
        AfterEachCallback {

    private static final Namespace NAMESPACE = Namespace.create( "com.tngtech.jgiven" );

    private static final String REPORT_MODEL = "report-model";

    @Override
    public void beforeAll( ContainerExtensionContext context ) throws Exception {
        ReportModel reportModel = new ReportModel();
        reportModel.setTestClass( context.getTestClass().get() );
        if( !context.getDisplayName().equals( context.getTestClass().get().getSimpleName() ) ) {
            reportModel.setName( context.getDisplayName() );
        }
        context.getStore( NAMESPACE ).put( REPORT_MODEL, reportModel );

        ConfigurationUtil.getConfiguration( context.getTestClass().get() )
            .configureTag( Tag.class )
            .description( "JUnit 5 Tag" )
            .color( "orange" );
    }

    @Override
    public void afterAll( ContainerExtensionContext context ) throws Exception {
        ReportModelHolder.get().removeReportModelOfCurrentThread();
        new CommonReportHelper().finishReport( (ReportModel) context.getStore( NAMESPACE ).get( REPORT_MODEL ) );
    }

    @Override
    public void beforeEach( TestExtensionContext context ) throws Exception {
        ReportModel reportModel = (ReportModel) context.getStore( NAMESPACE ).get( REPORT_MODEL );
        ReportModelHolder.get().setReportModelOfCurrentThread( reportModel );

        if( isTestFactory( context ) ) {
            return;
        }

        List<NamedArgument> args = new ArrayList<NamedArgument>();
        getScenario().startScenario( context.getTestClass().get(), context.getTestMethod().get(), args );
    }

    private boolean isTestFactory( TestExtensionContext context ) {
        return context.getTestMethod().get().getAnnotation( TestFactory.class ) != null;
    }

    @Override
    public void afterEach( TestExtensionContext context ) throws Exception {
        ReportModelHolder.get().removeReportModelOfCurrentThread();

        if( isTestFactory( context ) ) {
            return;
        }

        ScenarioBase scenario = getScenario();
        try {
            if( context.getTestException().isPresent() ) {
                scenario.getExecutor().failed( context.getTestException().get() );
            }
            scenario.finished();

            // ignore test when scenario is not implemented
            Assumptions.assumeTrue( EnumSet.of( SUCCESS, FAILED ).contains( scenario.getScenarioModel().getExecutionStatus() ) );

        } catch( Exception e ) {
            throw e;
        } catch( Throwable e ) {
            throw new RuntimeException( e );
        } finally {
            ScenarioHolder.get().removeScenarioOfCurrentThread();
        }

    }

    private ScenarioBase getScenario() {
        return ScenarioHolder.get().getScenarioOfCurrentThread();
    }

    @Override
    public void postProcessTestInstance( Object testInstance, ExtensionContext context ) throws Exception {
        ScenarioBase scenario = ScenarioHolder.get().getScenarioOfCurrentThread();

        if( scenario == null ) {
            if( testInstance instanceof ScenarioTestBase ) {
                scenario = ( (ScenarioTestBase) testInstance ).getScenario();
            } else {
                scenario = new ScenarioBase();
            }
            ReportModel reportModel = (ReportModel) context.getStore( NAMESPACE ).get( REPORT_MODEL );
            scenario.setModel( reportModel );
            ScenarioHolder.get().setScenarioOfCurrentThread( scenario );
        }

        scenario.getExecutor().injectStages( testInstance );
        scenario.getExecutor().readScenarioState( testInstance );
    }
}
