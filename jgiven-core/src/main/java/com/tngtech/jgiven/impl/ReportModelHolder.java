package com.tngtech.jgiven.impl;

import com.tngtech.jgiven.report.model.ReportModel;

public class ReportModelHolder {
    private final ThreadLocal<ReportModel> reportModel = new ThreadLocal<ReportModel>();

    private static final ReportModelHolder INSTANCE = new ReportModelHolder();

    public static ReportModelHolder get() {
        return INSTANCE;
    }

    public ReportModel getReportModelOfCurrentThread() {
        return reportModel.get();
    }

    public void setReportModelOfCurrentThread(ReportModel reportModel) {
        this.reportModel.set(reportModel);
    }

    public void removeReportModelOfCurrentThread() {
        reportModel.remove();
    }

}
