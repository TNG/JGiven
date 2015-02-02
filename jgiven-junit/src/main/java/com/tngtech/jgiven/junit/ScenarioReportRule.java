package com.tngtech.jgiven.junit;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.tngtech.jgiven.report.impl.CommonReportHelper;

public class ScenarioReportRule extends TestWatcher {
    @Override
    protected void finished( Description description ) {
        new CommonReportHelper().finishReport( ScenarioModelHolder.getInstance().getAndRemoveReportModel( description.getTestClass() ) );
    }
}
