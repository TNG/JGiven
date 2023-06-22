package com.tngtech.jgiven.gradle.internal;

import com.tngtech.jgiven.gradle.JGivenReport;
import com.tngtech.jgiven.report.ReportGenerator;
import org.gradle.api.Task;

public abstract class JGivenAsciiDocReportImpl extends AbstractJGivenReportImpl implements JGivenReport {
  private static final ReportGenerator.Format SUPPORTED_FORMAT = ReportGenerator.Format.ASCIIDOC;
  public static final String NAME = SUPPORTED_FORMAT.formatName();

  protected JGivenAsciiDocReportImpl(Task task) {
    super(NAME, task, "index.asciidoc");
  }

  @Override
  public ReportGenerator.Format getFormat() {
    return SUPPORTED_FORMAT;
  }
}
