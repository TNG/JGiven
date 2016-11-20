package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.Reader;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioJsonReader implements Function<File, ReportModel> {
    /**
     * @throws JsonReaderException in case there was an error while reading the file.
     */
    @Override
    public ReportModel apply( File file ) {
        Reader reader = null;
        try {
            reader = Files.newReader( file, Charsets.UTF_8 );
            return new Gson().fromJson( reader, ReportModel.class );
        } catch( Exception e ) {
            throw new JsonReaderException( file, e );
        } finally {
            ResourceUtil.close( reader );
        }
    }

    public static class JsonReaderException extends RuntimeException {
        public final File file;

        public JsonReaderException( File file, Exception source ) {
            super( source );
            this.file = file;
        }
    }
}
