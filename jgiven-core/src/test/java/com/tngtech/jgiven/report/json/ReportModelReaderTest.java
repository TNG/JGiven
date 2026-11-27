package com.tngtech.jgiven.report.json;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.report.AbstractReportConfig;
import java.io.File;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportModelReaderTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Mock
    private AbstractReportConfig config;

    @Test
    public void wrong_json_files_are_handled_gracefully() throws Exception {
        var folder = tmpFolder.newFolder();

        Files.asCharSink( new File( folder, "wrong.json" ), Charsets.UTF_8 ).write( "no json");

        when(config.getSourceDir()).thenReturn(tmpFolder.getRoot());
        var reportModelReader = new ReportModelReader(config);

        assertThatThrownBy(reportModelReader::readDirectory).isInstanceOf(JGivenWrongUsageException.class)
                .hasMessageContaining("Error while reading file");
    }
}
