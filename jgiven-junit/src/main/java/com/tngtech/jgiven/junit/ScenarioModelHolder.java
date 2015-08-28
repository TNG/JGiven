package com.tngtech.jgiven.junit;

import java.util.Map;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.report.model.ReportModel;

/**
 * Holds report models mapped by test class names.
 * This class is a singleton and thread-safe
 */
public class ScenarioModelHolder {

    private final static ScenarioModelHolder instance = new ScenarioModelHolder();

    private final Map<Class<?>, ReportModel> reportModels = Maps.newHashMap();

    public static ScenarioModelHolder getInstance() {
        return instance;
    }

    /**
     * Returns the {@link com.tngtech.jgiven.report.model.ReportModel} for the given test class.
     * If there is no report model yet, creates a new one.
     *
     * @param testClass the test class to get the report model for
     * @return the report model for the given test class
     */
    public ReportModel getReportModel( Class<? extends Object> testClass ) {
        synchronized( reportModels ) {
            ReportModel reportModel = reportModels.get( testClass );

            if( reportModel == null ) {
                reportModel = new ReportModel();
                reportModel.setTestClass( testClass );

                reportModels.put( testClass, reportModel );
            }

            return reportModel;
        }
    }

    /**
     * Returns the {@link com.tngtech.jgiven.report.model.ReportModel} for the given test class and removes it.
     */
    public ReportModel getAndRemoveReportModel( Class<?> testClass ) {
        synchronized( reportModels ) {
            ReportModel reportModel = reportModels.get( testClass );
            reportModels.remove( testClass );
            return reportModel;

        }
    }
}
