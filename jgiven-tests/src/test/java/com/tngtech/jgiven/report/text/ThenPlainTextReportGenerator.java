package com.tngtech.jgiven.report.text;

import com.tngtech.jgiven.report.ThenReportGenerator;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenPlainTextReportGenerator<SELF extends ThenPlainTextReportGenerator<?>> extends ThenReportGenerator<SELF> {

    public SELF a_text_file_exists_for_each_test_class() {
        for( ReportModel model : reportModels ) {
            a_file_with_name_$_exists( model.getClassName() + ".feature" );
        }
        return self();
    }
}
