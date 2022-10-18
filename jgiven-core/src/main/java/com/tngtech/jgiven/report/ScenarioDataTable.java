package com.tngtech.jgiven.report;

import com.tngtech.jgiven.report.model.ExecutionStatus;

import java.util.List;

public interface ScenarioDataTable {
    /**
     * The place holders of the data table
     */
    List<String> placeHolders();

    /**
     * The rows of the table, not including the header
     */
    List<Row> rows();

    /**
     * Represents one case of a scenario
     */
    interface Row {
        /**
         * The row number starting from 1
         */
        int nr();

        /**
         * The execution status of the case
         */
        ExecutionStatus status();

        /**
         * The argument values of the case
         */
        List<String> arguments();
    }
}
