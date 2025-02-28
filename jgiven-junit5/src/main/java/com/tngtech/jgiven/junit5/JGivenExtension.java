package com.tngtech.jgiven.junit5;

import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.report.impl.CommonReportHelper;
import com.tngtech.jgiven.report.model.ReportModel;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;

import java.util.EnumSet;
import java.util.Optional;

import static com.tngtech.jgiven.impl.util.ThrowableUtil.isAssumptionException;
import static com.tngtech.jgiven.report.model.ExecutionStatus.*;

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
 */
public class JGivenExtension implements
    TestInstancePostProcessor,
    BeforeAllCallback,
    AfterAllCallback,
    BeforeEachCallback,
    AfterTestExecutionCallback {

    private static final Namespace NAMESPACE = Namespace.create("com.tngtech.jgiven");

    private static final String REPORT_MODEL = "report-model";

    @Override
    public void beforeAll(ExtensionContext context) {
        validatePerMethodLifecycle(context);

        ReportModel reportModel = new ReportModel();
        reportModel.setTestClass(context.getTestClass().orElseThrow());
        if (!context.getDisplayName().equals(context.getTestClass().orElseThrow().getSimpleName())) {
            reportModel.setName(context.getDisplayName());
        }
        context.getStore(NAMESPACE).put(REPORT_MODEL, reportModel);

        AbstractJGivenConfiguration configuration = ConfigurationUtil.getConfiguration(context.getTestClass().get());
        if (configuration.getTagConfiguration(Tag.class) == null) {
            configuration.configureTag(Tag.class)
                .description("JUnit 5 Tag")
                .color("orange");
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        ScenarioHolder.get().removeScenarioOfCurrentThread();
        new CommonReportHelper().finishReport((ReportModel) context.getStore(NAMESPACE).get(REPORT_MODEL));
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        getScenario().startScenario(context.getTestClass().orElseThrow(), context.getTestMethod().orElseThrow(),
            ArgumentReflectionUtil.getNamedArgs(context));
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ScenarioBase scenario = getScenario();
        try {
            reportPotentialExecutionException(scenario, context);
            scenario.finished();

            // ignore test when scenario is not implemented
            Assumptions.assumeTrue(
                EnumSet.of(SUCCESS, FAILED, ABORTED).contains(scenario.getScenarioModel().getExecutionStatus()));

        } catch (Exception e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            ScenarioHolder.get().removeScenarioOfCurrentThread();
        }
    }

    private void reportPotentialExecutionException(ScenarioBase scenario, ExtensionContext context) {
        Optional<Throwable> exception = context.getExecutionException();
        if (exception.isEmpty()) {
            return;
        }
        if (isAssumptionException(exception.get())) {
            scenario.getExecutor().aborted(exception.get());
        } else if (!isAssumptionException(exception.get())) {
            scenario.getExecutor().failed(exception.get());
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        ScenarioBase currentScenario = ScenarioHolder.get().getScenarioOfCurrentThread();

        ScenarioBase scenario;
        if (testInstance instanceof ScenarioTestBase) {
            scenario = ((ScenarioTestBase<?, ?, ?>) testInstance).getScenario();
        } else {
            scenario = currentScenario == null ? new ScenarioBase() : currentScenario;
        }

        if (scenario != currentScenario) {
            ReportModel reportModel = (ReportModel) context.getStore(NAMESPACE).get(REPORT_MODEL);
            scenario.setModel(reportModel);
            ScenarioHolder.get().setScenarioOfCurrentThread(scenario);
        }

        scenario.getExecutor().injectStages(testInstance);
        scenario.getExecutor().readScenarioState(testInstance);
    }

    private void validatePerMethodLifecycle(ExtensionContext context) {
        if (isPerClassLifecycle(context)) {
            throw new JGivenWrongUsageException(
                "JGiven does not support keeping a test instance over multiple scenarios. Please use Lifecycle '"
                    + TestInstance.Lifecycle.PER_METHOD + "'.");
        }
    }

    private boolean isPerClassLifecycle(ExtensionContext context) {
        return context.getTestInstanceLifecycle()
            .filter(lifecycle -> TestInstance.Lifecycle.PER_CLASS == lifecycle)
            .isPresent();
    }

    private ScenarioBase getScenario() {
        return ScenarioHolder.get().getScenarioOfCurrentThread();
    }
}
