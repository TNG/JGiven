package com.tngtech.jgiven.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import com.tngtech.jgiven.relocated.guava.io.Files;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.report.model.ReportModel;

import javax.xml.bind.DatatypeConverter;

public class ThenReportGenerator<SELF extends ThenReportGenerator<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;
    private File currentFile;

    public SELF a_file_with_name_$_exists(@Quoted String name) {
        a_file_$_exists_in_folder_$(name,"");
        return self();
    }

    public SELF a_file_$_exists_in_folder_$(@Quoted String name, @Quoted String folder) {
        currentFile = new File(new File(targetReportDir, folder), name);
        assertThat(currentFile).exists();
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

    public SELF content(@Quoted String content) {
        assertThat(currentFile).hasContent(content);
        return self();
    }

    public SELF binary_content(@Quoted String base64content) {
        assertThat(currentFile).hasBinaryContent(DatatypeConverter.parseBase64Binary(base64content));
        return self();
    }

}
