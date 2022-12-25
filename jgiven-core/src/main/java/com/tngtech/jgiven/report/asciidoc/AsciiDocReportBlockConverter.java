package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.ReportBlockConverter;
import com.tngtech.jgiven.report.ScenarioDataTable;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import com.tngtech.jgiven.report.model.StepStatus;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

class AsciiDocReportBlockConverter implements ReportBlockConverter {

    private final PrintWriter writer;

    AsciiDocReportBlockConverter(PrintWriter printWriter) {
        this.writer = printWriter;
    }

    @Override
    public void className(String className) {
        writer.println("=== " + className);
        writer.println();
    }

    @Override
    public void reportDescription(String description) {
        writer.println(description);
        writer.println();
    }

    @Override
    public void reportSummary(int successfulScenarios, int failedScenarios, int pendingScenarios, int totalScenarios, Duration duration) {
        writer.print(successfulScenarios + " Successful, ");
        writer.print(failedScenarios + " Failed, ");
        writer.print(pendingScenarios + " Pending, ");
        writer.print(totalScenarios + " Total ");
        writer.println(humanReadableDuration(duration));
        writer.println();
    }

    @Override
    public void scenarioTitle(String title, String extendedDescription,
                              ExecutionStatus executionStatus, Duration duration,
                              Set<String> tagNames) {
        writer.println();
        writer.println("==== " + WordUtil.capitalize(title));
        writer.println();
        writer.println("[" + executionStatus + "] " + humanReadableDuration(duration));
        writer.println();

        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            writer.println(extendedDescription);
            writer.println();
        }

        if (!tagNames.isEmpty()) {
            writer.print("Tags: ");
            writer.println(tagNames.stream().map(tag -> "_" + tag + "_").collect(joining(", ")));
            writer.println();
        }
    }

    @Override
    public void caseHeader(int caseNr, List<String> parameterNames, List<String> caseArguments) {
        writer.println();
        writer.print("===== Case " + caseNr + ": ");
        for (int i = 0; i < parameterNames.size(); i++) {
            writer.print(parameterNames.get(i) + " = " + caseArguments.get(i) + ", ");
        }
        writer.println("");
        writer.println("");
    }

    @Override
    public void caseHeader() {
        writer.println("[.steps]");
    }

    @Override
    public void dataTable(ScenarioDataTable scenarioDataTable) {
        writer.println();
        writer.println(".Cases");
        writer.println("[options=\"header\"]");
        writer.println("|===");

        writer.print("| # ");
        for (String placeHolder : scenarioDataTable.placeHolders()) {
            writer.print(" | " + placeHolder);
        }
        writer.println(" | Status");

        for (ScenarioDataTable.Row row : scenarioDataTable.rows()) {
            writer.print("| " + row.nr());

            for (String value : row.arguments()) {
                writer.print(" | " + escapeTableValue(value));
            }

            writer.println(" | " + row.status());
        }
        writer.println("|===");
    }

    @Override
    public void scenarioEnd() {
    }

    @Override
    public void sectionTitle(String title) {
        writer.println();
        writer.println("." + title);
    }

    @Override
    public void stepStart(int depth) {
        for (int i = 0; i <= depth; i++) {
            writer.print("*");
        }
        writer.print(" ");
    }

    @Override
    public void stepEnd(boolean lastWordWasDataTable, StepStatus status, Duration duration, String extendedDescription) {
        writer.print("[.right]#[" + status + "] " + humanReadableDuration(duration) + "#");
        stepEnd(lastWordWasDataTable, extendedDescription);
    }
    @Override
    public void stepEnd(boolean lastWordWasDataTable, String extendedDescription) {
        if (extendedDescription != null && !extendedDescription.isEmpty()) {
            writer.println(" +");
            writer.println("_" + extendedDescription + "_");
        } else {
            writer.println();
        }
    }

    @Override
    public void introWord(String value) {
        writer.print("[.introWord]*" + value + "* ");
    }

    @Override
    public void stepArgumentPlaceHolder(String placeHolderValue) {
        writer.print("[.stepArgument]*<" + placeHolderValue + ">* ");
    }

    @Override
    public void stepCaseArgument(String caseArgumentValue) {
        writer.print("[.stepArgument]*" + escapeArgument(caseArgumentValue) + "* ");
    }

    @Override
    public void stepArgument(String argumentValue, boolean differs) {
        if (argumentValue.contains("\n")) {
            writer.println("\n");
            writer.println("[.stepArgument]");
            writer.println("....");
            writer.println(argumentValue);
            writer.println("....");
            writer.println();
        } else {
            writer.print("[.stepArgument]_" + escapeArgument(argumentValue) + "_ ");
        }
    }

    @Override
    public void stepDataTableArgument(DataTable dataTable) {
        List<List<String>> rows = dataTable.getData();
        if (rows.isEmpty()) {
            return;
        }

        String colsSpec;
        if (dataTable.hasVerticalHeader()) {
            colsSpec = "h," + generate(() -> "1").limit(dataTable.getColumnCount() - 1).collect(joining(","));
        } else {
            colsSpec = generate(() -> "1").limit(dataTable.getColumnCount()).collect(joining(","));
        }

        writer.println();
        writer.println("+");
        if (dataTable.hasHorizontalHeader()) {
            writer.println("[.stepArgument%header,cols=\"" + colsSpec + "\"]");
        } else {
            writer.println("[.stepArgument,cols=\"" + colsSpec + "\"]");
        }

        writer.println("|===");
        rows.forEach(row -> {
            row.forEach(cell -> writer.println("|" + cell));
            writer.println();
        });
        writer.println("|===");
        writer.println();
    }

    @Override
    public void stepWord(String value, boolean differs) {
        if (differs) {
            writer.print("#" + value + "# ");
        } else{
            writer.print(value + " ");
        }
    }

    private String escapeTableValue(String value) {
        return escapeArgument(value.replace("|", "\\|"));
    }

    private String escapeArgument(String argumentValue) {
        return "pass:[" + argumentValue + "]";
    }


    private static String humanReadableDuration(Duration duration) {
        return "(" + duration.getSeconds() + "s " + duration.getNano() / 1000000 + "ms)";
    }
}