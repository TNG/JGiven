package com.tngtech.jgiven.report.json;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tngtech.jgiven.relocated.guava.base.Charsets;
import com.tngtech.jgiven.relocated.guava.base.Throwables;
import com.tngtech.jgiven.relocated.guava.io.Files;
import com.tngtech.jgiven.relocated.gson.GsonBuilder;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioJsonWriter {
    private static final Logger log = LoggerFactory.getLogger( ScenarioJsonWriter.class );
    private final ReportModel model;

    public ScenarioJsonWriter( ReportModel model ) {
        this.model = model;
    }

    public void write( File file ) {
        String json = toString();
        try {
            Files.write( json, file, Charsets.UTF_8 );
            log.debug( "Written JSON to file {}, {}", file, json );
        } catch( IOException e ) {
            Throwables.propagate( e );
        }
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson( model );
    }
}
