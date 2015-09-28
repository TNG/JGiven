package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.tngtech.jgiven.report.ThenReportGenerator;

public class ThenHtml5ReportGenerator<SELF extends ThenHtml5ReportGenerator<SELF>> extends ThenReportGenerator<SELF> {

    public static final String META_DATA_PATTERN = "jgivenReport.setMetaData\\((.*)\\);";

    public void the_metaData_file_has_title_set_to( String title ) throws IOException {
        String metaDataContent = Files.toString( new File( new File( targetReportDir, "data" ), "metaData.js" ), Charsets.UTF_8 );

        Matcher matcher = Pattern.compile( META_DATA_PATTERN ).matcher( metaDataContent );
        assertThat( metaDataContent ).matches( META_DATA_PATTERN );

        matcher.matches();
        String metaDataObject = matcher.group( 1 );

        Html5ReportGenerator.MetaData metaData = new Gson()
            .fromJson( metaDataObject, Html5ReportGenerator.MetaData.class );

        assertThat( metaData.title ).isEqualTo( title );
    }
}
