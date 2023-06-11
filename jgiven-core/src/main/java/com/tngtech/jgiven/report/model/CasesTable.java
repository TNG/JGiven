package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents multiple scenario cases as a table structure for simpler reporting.
 */
public class CasesTable {
    private final List<String> placeHolders;
    private final boolean withDescriptions;
    private final List<CaseRow> rows;

    /**
     * Create a new CasesTable from its parts.
     *
     * @param placeHolders the placeholders for the header
     * @param withDescriptions are descriptions included
     * @param rows the rows representing the ScenarioCaseModels
     */
    public CasesTable(final List<String> placeHolders, final boolean withDescriptions, final List<CaseRow> rows) {
        this.placeHolders = placeHolders;
        this.withDescriptions = withDescriptions;
        this.rows = rows;
    }

    /**
     * The placeholders of the data table.
     */
    public List<String> placeholders() {
        return Collections.unmodifiableList(placeHolders);
    }

    public boolean hasDescriptions() {
        return withDescriptions;
    }

    /**
     * The rows of the table, not including the header.
     */
    public List<CaseRow> rows() {
        return Collections.unmodifiableList(rows);
    }

    /**
     * Represents one case of a scenario.
     */
    public static class CaseRow {

        private final int rowNumber;
        private final String description;
        private final List<String> arguments;
        private final ExecutionStatus executionStatus;
        private final long durationInNanos;
        private final String errorMessage;
        private final List<String> stackTrace;

        /**
         * Create a new row representing a single ScenarioCaseModel.
         *
         * @param rowNumber the case number
         * @param description the case's description
         * @param arguments the case's arguments
         * @param executionStatus the case's execution result
         * @param durationInNanos the duration it took to execute the case
         * @param errorMessage the case's error message if any
         * @param stackTrace the case's stackTrace if any
         */
        public CaseRow(final int rowNumber,
                final String description,
                final List<String> arguments,
                final ExecutionStatus executionStatus,
                final long durationInNanos,
                final String errorMessage,
                final List<String> stackTrace) {

            this.rowNumber = rowNumber;
            this.description = description;
            this.arguments = arguments;
            this.executionStatus = executionStatus;
            this.durationInNanos = durationInNanos;
            this.errorMessage = errorMessage;
            this.stackTrace = stackTrace;
        }

        /**
         * The row number starting from 1.
         */
        public int rowNumber() {
            return rowNumber;
        }

        /**
         * The case description.
         */
        public Optional<String> description() {
            return Optional.ofNullable(description);
        }

        /**
         * The argument values of the case.
         */
        public List<String> arguments() {
            return Collections.unmodifiableList(arguments);
        }

        /**
         * The execution status of the case.
         */
        public ExecutionStatus status() {
            return executionStatus;
        }

        /**
         * The duration of the case.
         */
        public long getDurationInNanos() {
            return durationInNanos;
        }

        /**
         * The error message of the case if any.
         */
        public Optional<String> errorMessage() {
            return Optional.ofNullable(errorMessage);
        }

        /**
         *  The stack trace of the case if any.
         */
        public List<String> stackTrace() {
            if (stackTrace == null) {
                return Collections.emptyList();
            }
            return Collections.unmodifiableList(stackTrace);
        }
    }
}
