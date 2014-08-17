package com.tngtech.jgiven.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenReportGenerator<SELF extends ThenReportGenerator<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;

    public SELF a_file_with_name_$_exists( String name ) {
        assertThat( new File( targetReportDir, name ) ).exists();
        return self();
    }
}
