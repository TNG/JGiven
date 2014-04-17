package com.tngtech.jgiven.report.json;

import java.io.File;

import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.FilePredicates;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class JsonModelTraverser {
    public void traverseModels( File sourceDir, ReportModelFileHandler handler ) {
        for( ReportModelFile f : Files.fileTreeTraverser().breadthFirstTraversal( sourceDir )
            .filter( FilePredicates.endsWith( ".json" ) )
            .transform( new ReportModelFileReader() ) ) {
            handler.handleReportModel( f.model, f.file );
        }
    }
}
