package com.tngtech.jgiven.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.io.Files;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenReportGenerator<SELF extends ThenReportGenerator<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;

    public SELF a_file_with_name_$_exists(@Quoted String name) {
        assertThat(new File(targetReportDir, name)).exists();
        return self();
    }

    public SELF a_file_$_exists_in_folder_$(@Quoted String name, @Quoted String folder) {
        assertThat(new File(new File(targetReportDir, folder), name)).exists();
        return self();
    }

    public SELF file_$_contains_pattern(@Quoted String fileName, @Quoted final String regexp) throws IOException {
        String content = Files.asCharSource(new File(targetReportDir, fileName), Charset.forName("utf8")).read();
        Pattern pattern = Pattern.compile(".*" + regexp + ".*", Pattern.MULTILINE | Pattern.DOTALL);

        assertThat(pattern.matcher(regexp).matches()).as("file " + fileName + " does not contain " + regexp).isTrue();
        return self();
    }

    public SELF file_$_contains(@Quoted String fileName, @Quoted final String string) throws IOException {
        String content = Files.asCharSource(new File(targetReportDir, fileName), Charset.forName("utf8")).read();
        assertThat(content).as("file " + fileName + " does not contain " + string).contains(string);
        return self();
    }
}
