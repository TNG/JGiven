package com.tngtech.jgiven;

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

    private static final Map<String, ReportModel> modelHolder = new ConcurrentHashMap<>();

    public static Optional<ReportModel> getReportModelFor(String uniqueId) {
        return Optional.ofNullable(modelHolder.get(uniqueId));
    }


    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        Optional.ofNullable(ScenarioHolder.get())
            .map(ScenarioHolder::getScenarioOfCurrentThread)
            .map(ScenarioBase::getModel)
            .ifPresent(model -> modelHolder.put(context.getUniqueId(), model));
        super.afterTestExecution(context);
    }
}
