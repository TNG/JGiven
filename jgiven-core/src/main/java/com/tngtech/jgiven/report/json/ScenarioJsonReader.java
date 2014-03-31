package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.FileReader;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioJsonReader implements Function<File, ReportModel> {
    @Override
    public ReportModel apply( File file ) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader( file );
            return new Gson().fromJson( fileReader, ReportModel.class );
        } catch( Exception e ) {
            throw Throwables.propagate( e );
        } finally {
            ResourceUtil.close( fileReader );
        }
    }
}
