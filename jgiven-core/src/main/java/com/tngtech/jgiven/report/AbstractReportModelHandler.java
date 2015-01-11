package com.tngtech.jgiven.report;

import java.util.List;

import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.model.*;

public class AbstractReportModelHandler {

    public void handle( ReportModel reportModel, ReportModelHandler handler ) {
        reportModel.accept( new ReportModelHandlerVisitor( handler ) );
    }

    private static class ReportModelHandlerVisitor extends ReportModelVisitor {

        private final ReportModelHandler handler;
        private boolean isMultiCase;
        private boolean hasDataTable;
        private ScenarioModel currentScenarioModel;
        private boolean skipCase;

        public ReportModelHandlerVisitor( ReportModelHandler handler ) {
            this.handler = handler;
        }

        @Override
        public void visit( ReportModel reportModel ) {
            handler.className( reportModel.getClassName() );
            if( reportModel.getDescription() != null ) {
                handler.reportDescription( reportModel.getDescription() );
            }
        }

        @Override
        public void visit( ScenarioModel scenarioModel ) {
            handler.scenarioTitle( scenarioModel.getDescription() );

            this.currentScenarioModel = scenarioModel;
            this.isMultiCase = scenarioModel.getScenarioCases().size() > 1;
            this.hasDataTable = scenarioModel.isCasesAsTable();
        }

        @Override
        public void visitEnd( ScenarioModel scenarioModel ) {
            if( hasDataTable ) {
                handler.dataTable( new ScenarioDataTableImpl( scenarioModel ) );
            }
            handler.scenarioEnd();
        }

        @Override
        public void visit( ScenarioCaseModel scenarioCase ) {
            if( scenarioCase.getCaseNr() > 1 && hasDataTable ) {
                this.skipCase = true;
                return;
            }
            this.skipCase = false;

            if( isMultiCase && !hasDataTable ) {
                handler.caseHeader( scenarioCase.getCaseNr(),
                    currentScenarioModel.getExplicitParameters(), scenarioCase.getExplicitArguments() );
            }
        }

        @Override
        public void visit( StepModel stepModel ) {
            if( skipCase ) {
                return;
            }

            handler.stepStart();

            for( Word word : stepModel.getWords() ) {
                if( word.isIntroWord() ) {
                    handler.introWord( word.getValue() );
                } else {
                    if( word.isArg() ) {
                        if( word.getArgumentInfo().isParameter() ) {
                            if( hasDataTable ) {
                                handler.stepArgumentPlaceHolder( word.getArgumentInfo().getParameterName() );
                            } else {
                                handler.stepCaseArgument( word.getFormattedValue() );
                            }
                        } else {
                            if( word.getArgumentInfo().isDataTable() ) {
                                handler.stepDataTableArgument( word.getArgumentInfo().getDataTable() );
                            } else {
                                handler.stepArgument( word.getFormattedValue(), word.isDifferent() );
                            }
                        }
                    } else {
                        handler.stepWord( word.getFormattedValue(), word.isDifferent() );
                    }
                }
            }

            handler.stepEnd();
        }

        private static class ScenarioDataTableImpl implements ScenarioDataTable {
            private final ScenarioModel scenarioModel;

            private ScenarioDataTableImpl( ScenarioModel scenarioModel ) {
                this.scenarioModel = scenarioModel;
            }

            @Override
            public List<String> placeHolders() {
                return scenarioModel.getDerivedParameters();
            }

            @Override
            public List<Row> rows() {
                List<Row> rows = Lists.newArrayList();
                for( ScenarioCaseModel caseModel : scenarioModel.getScenarioCases() ) {
                    rows.add( new RowImpl( caseModel ) );
                }
                return rows;
            }

            private static class RowImpl implements Row {

                private final ScenarioCaseModel caseModel;

                public RowImpl( ScenarioCaseModel caseModel ) {
                    this.caseModel = caseModel;
                }

                @Override
                public int nr() {
                    return caseModel.getCaseNr();
                }

                @Override
                public ExecutionStatus status() {
                    return caseModel.getExecutionStatus();
                }

                @Override
                public List<String> arguments() {
                    return caseModel.getDerivedArguments();
                }
            }
        }
    }

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
        public interface Row {
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
             *
             */
            List<String> arguments();
        }
    }
}
