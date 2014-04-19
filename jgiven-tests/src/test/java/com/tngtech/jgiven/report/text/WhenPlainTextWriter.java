package com.tngtech.jgiven.report.text;

import java.io.UnsupportedEncodingException;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class WhenPlainTextWriter {
    @ExpectedScenarioState
    ReportModel reportModel;

    @ProvidedScenarioState
    String plainTextOutput;

    public void the_plain_text_report_is_generated() throws UnsupportedEncodingException {
        plainTextOutput = PlainTextReporter.toString( reportModel );
        System.out.println( plainTextOutput );
    }

}
