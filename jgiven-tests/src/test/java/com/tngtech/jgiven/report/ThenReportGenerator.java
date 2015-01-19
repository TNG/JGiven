package com.tngtech.jgiven.report;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.report.model.ReportModel;

public class ThenReportGenerator<SELF extends ThenReportGenerator<?>> extends Stage<SELF> {

    @ExpectedScenarioState
    protected File targetReportDir;

    @ExpectedScenarioState
    protected List<ReportModel> reportModels;

    public SELF a_file_with_name_$_exists( String name ) {
        assertThat( new File( targetReportDir, name ) ).exists();
        return self();
    }

    public SELF file_$_contains( String fileName, final String regex ) throws IOException {
        ImmutableList<String> content = Files.asCharSource( new File( targetReportDir, fileName ), Charset.forName( "utf8" ) ).readLines();

        boolean match = FluentIterable.from( content ).anyMatch( new Predicate<String>() {
            @Override
            public boolean apply( String input ) {
                return input.matches( regex );
            }
        } );

        assertThat( match ).as( "file " + fileName + " does not contain " + regex ).isTrue();
        return self();
    }
}
