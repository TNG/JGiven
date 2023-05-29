package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.model.ExecutionStatus;
import java.util.List;

/**
 * Represents multiple scenario cases as a table structure for simpler reporting.
 */
public interface CasesTable {
    /**
     * The placeholders of the data table.
     */
    List<String> placeholders();

    /**
     * The rows of the table, not including the header.
     */
    List<Row> rows();

    /**
     * Represents one case of a scenario.
     */
    interface Row {
        /**
         * The row number starting from 1.
         */
        int nr();

        /**
         * The execution status of the case.
         */
        ExecutionStatus status();

        /**
         * The argument values of the case.
         */
        List<String> arguments();
    }
}
