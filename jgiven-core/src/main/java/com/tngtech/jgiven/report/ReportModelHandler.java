package com.tngtech.jgiven.report;

import java.util.List;

import com.tngtech.jgiven.report.AbstractReportModelHandler.ScenarioDataTable;
import com.tngtech.jgiven.report.model.DataTable;

public interface ReportModelHandler {
    void className( String className );

    /**
     * Invoked for the description of this report model.
     * <p>
     *     Is only invoked if there is actually an description
     * </p>
     * @param description the description, is never {@code null}
     */
    void reportDescription( String description );

    /**
     * Is invoked for the title of the scenario.
     * <p>
     * The title corresponds to the test method name
     * </p>
     *
     * @param title the title of the scenario
     */
    void scenarioTitle( String title );

    /**
     * Is invoked when a scenario has multiple cases, but no data table.
     * <p>
     * Plain Text Example:
     * <pre>
     *   Case 1: param1 = arg1, param2 = arg2
     * </pre>
     * It is guaranteed that {@code parameterNames.size() == caseArgument.size()}
     * @param caseNr         the number of the case, starting from 0
     * @param parameterNames the parameter names of the scenario
     * @param caseArguments  the arguments of the case
     */
    void caseHeader( int caseNr, List<String> parameterNames, List<String> caseArguments );

    /**
     * Is invoked at the end of a scenario, when the scenario has multiple case and a data table.
     *
     * @param scenarioDataTable the data table of the scenario
     */
    void dataTable( ScenarioDataTable scenarioDataTable );

    /**
     * Invoked when the scenario is finished
     */
    void scenarioEnd();

    /**
     * Invoked when a step starts
     */
    void stepStart();

    /**
     * Invoked when a step is finished
     */
    void stepEnd();

    /**
     * Invoked for intro words like given, when, then
     */
    void introWord( String value );

    /**
     * Invoked for step argument place holders.
     * <p>
     * This is only invoked when the scenario has a data table.
     * 
     * @param placeHolderValue the value of the place holder
     */
    void stepArgumentPlaceHolder( String placeHolderValue );

    /**
     * Invoked for step arguments that are also arguments of a case.
     * <p>
     * This is only invoked when the scenario has mutliple cases, but no data table    
     * </p>
     * @param caseArgumentValue the value of the argument
     */
    void stepCaseArgument( String caseArgumentValue );

    /**
     * Invoked for step arguments that are not arguments of a case
     *
     * @param argumentValue the value of the argument
     * @param differs whether this argument differs compared to other cases of the same scenario
     */
    void stepArgument( String argumentValue, boolean differs );

    /**
     * Invoked for step arguments that are data tables
     * @param dataTable the data table
     */
    void stepDataTableArgument( DataTable dataTable );

    /**
     * Invoked for plain words of a step
     * @param value the value of the word
     * @param differs whether this word differs compared to other cases of the same scenario
     */
    void stepWord( String value, boolean differs );
}
