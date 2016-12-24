package com.tngtech.jgiven.report.json;

import java.io.File;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.FilePredicates;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class JsonModelTraverser {

    /**
     * Reads all JSON files from {@code sourceDirectory} and invokes for each found file 
     * the {@link ReportModelFileHandler#handleReportModel} method of the given {@code handler}.
     * 
     * @param sourceDirectory the directory that contains the JSON files
     * @param handler the handler to be invoked for each file
     */
    public void traverseModels( File sourceDirectory, ReportModelFileHandler handler ) {
        for( ReportModelFile f : Files.fileTreeTraverser().breadthFirstTraversal( sourceDirectory )
            .filter( FilePredicates.endsWith( ".json" ) )
            .transform( new ReportModelFileReader() ) ) {
            handler.handleReportModel( f );
        }
    }
}
