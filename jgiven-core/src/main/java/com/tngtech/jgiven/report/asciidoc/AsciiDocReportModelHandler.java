package com.tngtech.jgiven.report.asciidoc;

import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.AbstractReportModelHandler;
import com.tngtech.jgiven.report.ReportModelHandler;
import com.tngtech.jgiven.report.model.DataTable;

import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;

class AsciiDocReportModelHandler implements ReportModelHandler {

    private final PrintWriter writer;

    AsciiDocReportModelHandler(PrintWriter printWriter) {
        this.writer = printWriter;
    }

    @Override
    public void className(String className) {
        writer.println("==== " + className + " ====\n");
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
        writer.println("(" + duration.getSeconds() + "s "+duration.getNano()/1000000 + "ms)");
        writer.println();
    }

    @Override
    public void scenarioTitle(String title) {
        writer.println("===== " + WordUtil.capitalize(title) + " =====\n");
    }

    @Override
    public void caseHeader(int caseNr, List<String> parameterNames, List<String> caseArguments) {
        writer.print("====== Case " + caseNr + ": ");
        for (int i = 0; i < parameterNames.size(); i++) {
            writer.print(parameterNames.get(i) + " = " + caseArguments.get(i));
        }
        writer.println(" ======\n");
    }

    @Override
    public void dataTable(AbstractReportModelHandler.ScenarioDataTable scenarioDataTable) {
        writer.println("\n.Cases");
        writer.println("[options=\"header\"]");
        writer.println("|===");

        writer.print("| # ");
        for (String placeHolder : scenarioDataTable.placeHolders()) {
            writer.print(" | " + placeHolder);
        }
        writer.println(" | Status");

        for (AbstractReportModelHandler.ScenarioDataTable.Row row : scenarioDataTable.rows()) {
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
        writer.println();
    }

    @Override
    public void stepStart() {
    }

    @Override
    public void stepEnd(boolean lastWordWasDataTable) {
        if (!lastWordWasDataTable) {
            writer.println("+");
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
        String colsSpec;
        if (dataTable.hasVerticalHeader()) {
            colsSpec = "h," + generate(() -> "1").limit(dataTable.getColumnCount() - 1).collect(joining(","));
        } else {
            colsSpec = generate(() -> "1").limit(dataTable.getColumnCount()).collect(joining(","));
        }

        writer.println();
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
        writer.print(value + " ");
    }

    private String escapeTableValue(String value) {
        return escapeArgument(value.replace("|", "\\|"));
    }

    private String escapeArgument(String argumentValue) {
        return "pass:[" + argumentValue + "]";
    }

}
