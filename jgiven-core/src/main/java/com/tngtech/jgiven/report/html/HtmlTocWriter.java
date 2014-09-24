package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.tngtech.jgiven.report.html.PackageTocBuilder.PackageToc;
import com.tngtech.jgiven.report.html.StaticHtmlReportGenerator.ModelFile;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Tag;

public class HtmlTocWriter {
    protected PrintWriter writer;
    private final Map<Tag, List<ScenarioModel>> tagMap;
    private final PackageToc packageToc;

    public HtmlTocWriter( Map<Tag, List<ScenarioModel>> tagMap, PackageToc packageToc ) {
        this.tagMap = tagMap;
        this.packageToc = packageToc;
    }

    public void writeToc( PrintWriter writer ) {
        this.writer = writer;
        writer.println( "<div id='col-container'>" );
        writer.println( "<div id='leftpane'>" );
        writer.println( "<div id='toc'>" );
        writeSearchInput();
        writePackages();
        writeTagLinks();
        writer.println( "</div> <!-- toc -->" );
        writer.println( "</div> <!-- leftpane -->" );
    }

    private void writeSearchInput() {
        writer.println( "<input id='searchfield' type='input' onkeydown='searchChanged(event)'></input>" );
    }

    private void writePackages() {
        writer.println( "<h3>Test Classes</h3>" );
        writer.println( "<ul>" );
        printPackageToc( "", packageToc );
        writer.println( "</ul>" );
    }

    private void printPackageTocs( Iterable<PackageToc> tocs ) {
        for( PackageToc toc : tocs ) {
            printPackageToc( "", toc );
        }
    }

    private void printPackageToc( String prefix, PackageToc toc ) {
        String name = prefix + toc.getLastName();
        if( toc.files.isEmpty() && toc.packages.size() == 1 ) {
            String newPrefix = name.equals( "" ) ? "" : name + ".";
            printPackageToc( newPrefix, toc.packages.get( 0 ) );
        } else {
            if( !toc.name.equals( "" ) ) {
                writer.print( "<li><h4 class='packagename' onclick='toggle(\"" + toc.name + "\")'>" + name + "</h4>" );
                String collapsed = name.equals( toc.name ) ? "" : "collapsed";
                writer.println( "<ul class='" + collapsed + "' id='" + toc.name + "'>" );
            }
            printPackageTocs( toc.packages );
            printClassLinks( toc.files );
            if( !toc.name.equals( "" ) ) {
                writer.print( "</ul></li>" );
            }
        }
    }

    private void printClassLinks( List<ModelFile> files ) {
        for( ModelFile modelFile : files ) {
            writeClassLink( modelFile );
        }
    }

    private void writeClassLink( ModelFile model ) {
        writer.print( format( "<li><a href='%s' >%s</a></li>",
            model.file.getName(),
            model.model.getSimpleClassName() ) );
    }

    private void writeTagLinks() {
        if( tagMap.isEmpty() ) {
            return;
        }

        ImmutableListMultimap<String, Tag> tags = getGroupedTags();

        writer.println( "<h3>Tags</h3>" );
        writer.println( "<ul>" );
        List<String> orderedKeys = Ordering.natural().sortedCopy( tags.keySet() );
        for( String key : orderedKeys ) {
            writer.println( "<li>" );
            String tagId = "tag" + key;
            writer.println( "<h4 onclick='toggle(\"" + tagId + "\")'>" + key + "</h4>" );
            writer.println( "<ul id='" + tagId + "' class='collapsed'>" );

            List<Tag> sortedTags = Ordering.usingToString().sortedCopy( tags.get( key ) );
            for( Tag tag : sortedTags ) {
                writeTagLink( tag, tagMap.get( tag ) );
            }
            writer.println( "</ul>" );
        }
        writer.println( "</ul>" );
    }

    private ImmutableListMultimap<String, Tag> getGroupedTags() {
        ImmutableListMultimap<String, Tag> multiMap = Multimaps.index( tagMap.keySet(), new Function<Tag, String>() {
            @Override
            public String apply( Tag input ) {
                return input.getName();
            }
        } );
        return multiMap;
    }

    public List<Tag> getSortedTags() {
        List<Tag> sortedTags = Lists.newArrayList( tagMap.keySet() );
        Collections.sort( sortedTags, new Comparator<Tag>() {
            @Override
            public int compare( Tag o1, Tag o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        } );
        return sortedTags;
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
