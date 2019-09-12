package com.tngtech.jgiven.report.asciidoc;

import com.google.common.base.Charsets;
import com.tngtech.jgiven.report.ThenReportGenerator;
import org.apache.commons.io.FileUtils;

import static org.assertj.core.api.Assertions.assertThat;


import java.io.IOException;

public class ThenAsciiDocReportGenerator <SELF extends ThenAsciiDocReportGenerator<SELF>> extends ThenReportGenerator<SELF>  {

    public SELF the_asciidoc_reporte_$_exists(String ascidocFile) {
        super.a_file_with_name_$_exists(ascidocFile);
        return self();
    }

    public SELF the_literal_block_is_added_$(String expectedLiteral) throws IOException {
        String content = FileUtils.readFileToString(super.currentFile, Charsets.UTF_8);

        assertThat(content).contains(expectedLiteral);
        return self();
    }

}
