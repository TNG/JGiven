package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.report.html.FrameBasedHtmlReportGenerator.ModelFile;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Tag;

public class HtmlTocWriter {
    protected PrintWriter writer;
    private final Map<Tag, List<ScenarioModel>> tagMap;
    private final List<ModelFile> models;

    public HtmlTocWriter( Map<Tag, List<ScenarioModel>> tagMap, List<ModelFile> models ) {
        this.tagMap = tagMap;
        this.models = models;
    }

    public void writeToc( PrintWriter writer ) {
        this.writer = writer;
        writer.println( "<div class='leftpane'>" );
        writer.println( "<div class='toc'>" );
        writeClassLinks();
        writeTagLinks();
        writer.println( "</div> <!-- toc -->" );
        writer.println( "</div> <!-- leftpane -->" );
    }

    private void writeClassLinks() {
        Comparator<ModelFile> comparator = new Comparator<ModelFile>() {
            @Override
            public int compare( ModelFile o1, ModelFile o2 ) {
                return o1.model.getSimpleClassName().compareTo( o2.model.getSimpleClassName() );
            }
        };
        List<ModelFile> sortedModels = Lists.newArrayList( models );
        Collections.sort( sortedModels, comparator );
        writer.println( "<h3>Test Classes</h3>" );
        writer.println( "<ul>" );
        for( ModelFile modelFile : sortedModels ) {
            writeClassLink( modelFile );
        }
        writer.println( "</ul>" );
    }

    private void writeClassLink( ModelFile model ) {
        writer.println( format( "<li><a href='%s' >%s</a>",
            model.file.getName(),
            model.model.getSimpleClassName() ) );
    }

    private void writeTagLinks() {
        if( tagMap.isEmpty() ) {
            return;
        }

        List<Tag> sortedTags = Lists.newArrayList( tagMap.keySet() );
        Collections.sort( sortedTags, new Comparator<Tag>() {
            @Override
            public int compare( Tag o1, Tag o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        } );

        writer.println( "<h3>Tags</h3>" );
        writer.println( "<ul>" );
        for( Tag tag : sortedTags ) {
            writeTagLink( tag, tagMap.get( tag ) );
        }
        writer.println( "</ul>" );
    }

    private void writeTagLink( Tag tag, List<ScenarioModel> list ) {
        writer.println( format( "<li><a href='%s'>%s</a>",
            tagToFilename( tag ),
            tag.toString() ) );
    }

    static String tagToFilename( Tag tag ) {
        String fileName = escape( tag.getName() );
        if( tag.getValue() != null ) {
            if( tag.getValue().getClass().isArray() ) {
                fileName += "-" + escape( Joiner.on( '-' ).join( (String[]) tag.getValue() ) );
            } else {
                fileName += "-" + escape( (String) tag.getValue() );
            }
        }
        return fileName.substring( 0, Math.min( fileName.length(), 255 ) ) + ".html";
    }

    static String escape( String string ) {
        return string.replaceAll( "[^\\p{Alnum}-]", "_" );
    }

}
