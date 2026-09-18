package com.tngtech.jgiven.report;

import com.google.common.io.BaseEncoding;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.report.model.ReportModel;
import java.io.File;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ThenReportGenerator<SELF extends ThenReportGenerator<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;
    protected File currentFile;

    public SELF a_file_with_name_$_exists(@Quoted String name) {
        a_file_$2_exists_in_folder_$1("",name);
        return self();
    }

    public SELF a_file_$2_exists_in_folder_$1(@Quoted String folder, @Quoted String name) {
        var targetFolder = new File(targetReportDir, folder);
        assertThat(targetFolder).as("File with name '%s'", name)
                .isDirectoryContaining(file -> name.equals(file.getName()));
        currentFile = targetFolder.toPath().resolve(name).toFile();
        return self();
    }

    public SELF file_$_contains_pattern(@Quoted String fileName, @Quoted final String regexp) {
        assertThat(new File(targetReportDir, fileName)).content(UTF_8).containsPattern(regexp);
        return self();
    }

    public SELF file_$_contains(@Quoted String fileName, @Quoted final String string) {
        assertThat(new File(targetReportDir, fileName)).content(UTF_8).contains(string);
        return self();
    }

    public SELF content(@Quoted String content) {
        assertThat(currentFile).hasContent(content);
        return self();
    }

    public SELF binary_content(@Quoted String base64content) {
        assertThat(currentFile).hasBinaryContent(BaseEncoding.base64().decode(base64content));
        return self();
    }

}
