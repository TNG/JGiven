package com.tngtech.jgiven.report.asciidoc;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.tngtech.jgiven.impl.util.PrintWriterUtil;
import com.tngtech.jgiven.impl.util.ResourceUtil;
import com.tngtech.jgiven.impl.util.WordUtil;
import com.tngtech.jgiven.report.AbstractReportGenerator;
import com.tngtech.jgiven.report.AbstractReportModelHandler;
import com.tngtech.jgiven.report.AbstractReportModelHandler.ScenarioDataTable;
import com.tngtech.jgiven.report.ReportModelHandler;
import com.tngtech.jgiven.report.model.DataTable;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelFile;

public class AsciiDocReportGenerator extends AbstractReportGenerator {

    private List<String> allFiles = Lists.newArrayList();

    @Override
    public void generate() {
        for( ReportModelFile reportModelFile : completeReportModel.getAllReportModels() ) {
            writeReportModelToFile( reportModelFile.model, reportModelFile.file );
        }
        generateIndexFile();
    }

    public void writeReportModelToFile( ReportModel model, File file ) {
        String targetFileName = Files.getNameWithoutExtension( file.getName() ) + ".asciidoc";

        allFiles.add( targetFileName );

        File targetFile = new File( targetDirectory, targetFileName );
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( targetFile );

        try {
            new AbstractReportModelHandler().handle( model, new AsciiDocReportModelVisitor( printWriter ) );
        } finally {
            ResourceUtil.close( printWriter );
        }
    }

    public void generateIndexFile() {
        PrintWriter printWriter = PrintWriterUtil.getPrintWriter( new File( targetDirectory, "index.asciidoc" ) );
        try {
            printWriter.println( "= JGiven Documentation =\n" );
            printWriter.println( ":toc: left\n" );
            printWriter.println( "== Scenarios ==\n" );
            printWriter.println( "=== Classes ===\n" );
            printWriter.println( "include::allClasses.asciidoc[]" );
        } finally {
            ResourceUtil.close( printWriter );
        }

        printWriter = PrintWriterUtil.getPrintWriter( new File( targetDirectory, "allClasses.asciidoc" ) );
        try {
            for( String fileName : allFiles ) {
                printWriter.println( "include::" + fileName + "[]\n" );
            }
        } finally {
            ResourceUtil.close( printWriter );

        }

    }

    class AsciiDocReportModelVisitor implements ReportModelHandler {

        private final PrintWriter writer;
        private boolean literalBlock;

        public AsciiDocReportModelVisitor( PrintWriter printWriter ) {
            this.writer = printWriter;
        }

        @Override
        public void className( String className ) {
            writer.println( "==== " + className + " ====\n" );
        }

        @Override
        public void reportDescription( String description ) {
            writer.println( description );
            writer.println();
        }

        @Override
        public void scenarioTitle( String title ) {
            writer.println( "===== " + WordUtil.capitalize( title ) + " =====\n" );
        }

        @Override
        public void caseHeader( int caseNr, List<String> parameterNames, List<String> caseArguments ) {
            writer.print( "====== Case " + caseNr + ": " );
            for( int i = 0; i < parameterNames.size(); i++ ) {
                writer.print( parameterNames.get( i ) + " = " + caseArguments.get( i ) );
            }
            writer.println( " ======\n" );
        }

        @Override
        public void dataTable( ScenarioDataTable scenarioDataTable ) {
            writer.println( "\n.Cases" );
            writer.println( "[options=\"header\"]" );
            writer.println( "|===" );

            writer.print( "| # " );
            for( String placeHolder : scenarioDataTable.placeHolders() ) {
                writer.print( " | " + placeHolder );
            }
            writer.println( " | Status" );

            for( ScenarioDataTable.Row row : scenarioDataTable.rows() ) {
                writer.print( "| " + row.nr() );

                for( String value : row.arguments() ) {
                    writer.print( " | " + escapeTableValue( value ) );
                }

                writer.println( " | " + row.status() );
            }
            writer.println( "|===" );

        }

        @Override
        public void scenarioEnd() {
            writer.println();
        }

        @Override
        public void stepStart() {
            literalBlock = false;
        }

        @Override
        public void stepEnd() {
            if( !literalBlock ) {
                writer.println( "+" );
            }
        }

        @Override
        public void introWord( String value ) {
            writer.print( value + " " );
            literalBlock = false;
        }

        @Override
        public void stepArgumentPlaceHolder( String placeHolderValue ) {
            writer.print( "*<" + placeHolderValue + ">* " );
            literalBlock = false;
        }

        @Override
        public void stepCaseArgument( String caseArgumentValue ) {
            writer.print( "*" + escapeArgument( caseArgumentValue ) + "* " );
            literalBlock = false;
        }

        @Override
        public void stepArgument( String argumentValue, boolean differs ) {
            if( argumentValue.contains( "\n" ) ) {
                writer.println( "\n" );
                writer.println( "[literal]" );
                writer.println( argumentValue );
                writer.println();
                literalBlock = true;
            } else {
                writer.print( escapeArgument( argumentValue ) + " " );
                literalBlock = false;
            }
        }

        @Override
        public void stepDataTableArgument( DataTable dataTable ) {
            writer.print( "NOT SUPPORTED IN ASCIIDOC YET" );
            literalBlock = false;
        }

        @Override
        public void stepWord( String value, boolean differs ) {
            writer.print( value + " " );
            literalBlock = false;
        }

        private String escapeTableValue( String value ) {
            return escapeArgument( value.replace( "|", "\\|" ) );
        }

        private String escapeArgument( String argumentValue ) {
            return "pass:[" + argumentValue + "]";
        }

    }

}
