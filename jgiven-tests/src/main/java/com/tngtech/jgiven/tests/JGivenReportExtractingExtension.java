package com.tngtech.jgiven.tests;

import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.impl.ScenarioHolder;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.report.model.ReportModel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Extracts the report Model from within the Test context before it gets deleted.
 */
public class JGivenReportExtractingExtension extends JGivenExtension {

    private static final Map<Class<?>, ReportModel> modelHolder = new ConcurrentHashMap<>();

    public static Optional<ReportModel> getReportModelFor(Class<?> testClass) {
        return Optional.ofNullable(modelHolder.get(testClass));
    }


    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Class<?> testClass = context.getTestClass()
            .orElseThrow(() -> new JGivenWrongUsageException("tests without test class are not supported yet"));
        Optional.ofNullable(ScenarioHolder.get())
            .map(ScenarioHolder::getScenarioOfCurrentThread)
            .map(ScenarioBase::getModel)
            .ifPresent(model -> modelHolder.put(testClass, model));
        super.afterTestExecution(context);
    }
}
