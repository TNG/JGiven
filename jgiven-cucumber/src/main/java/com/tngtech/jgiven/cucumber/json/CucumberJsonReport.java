package com.tngtech.jgiven.cucumber.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tngtech.jgiven.impl.util.ResourceUtil;

public class CucumberJsonReport {
    public List<CucumberFeature> features;

    public static CucumberJsonReport fromFile( File cucumberJsonReportFile ) throws IOException {
        BufferedReader bufferedReader = Files.asCharSource( cucumberJsonReportFile, Charsets.UTF_8 ).openBufferedStream();
        try {
            CucumberJsonReport report = new CucumberJsonReport();
            report.features = new Gson().fromJson( bufferedReader, new TypeToken<List<CucumberFeature>>() {}.getType() );
            return report;
        } finally {
            ResourceUtil.close( bufferedReader );
        }

    }
}
