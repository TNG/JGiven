package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.tngtech.jgiven.report.html.PackageTocBuilder.PackageToc;
import com.tngtech.jgiven.report.model.*;

public class HtmlTocWriter {
    protected PrintWriter writer;
    private final CompleteReportModel reportModel;
    private final PackageToc packageToc;
    private final ImmutableListMultimap<String, Tag> groupedTags;

    public HtmlTocWriter( CompleteReportModel reportModel, PackageToc packageToc ) {
        this.reportModel = reportModel;
        this.packageToc = packageToc;
        groupedTags = getGroupedTags();
    }

    public void writeToc( PrintWriter writer ) {
        this.writer = writer;
        writer.println( "<div id='col-container'>" );
        writer.println( "<div id='leftpane'>" );
        writer.println( "<div id='toc'>" );
        writer.println( "<i class='icon-cancel' onclick='hideToc()'></i>" );
        writeSearchInput();
        writeSummary();
        writePackages();
        writeTagLinks();
        writer.println( "</div> <!-- toc -->" );
        writer.println( "</div> <!-- leftpane -->" );
    }

    private void writeSummary() {
        writer.println( "<h3><a href='index.html'>Summary</a></h3>" );
        writer.println( "<ul>" );
        ReportStatistics totalStatistics = reportModel.getTotalStatistics();
        writeLink( totalStatistics.numScenarios, "all.html", "All Scenarios" );
        writeLink( totalStatistics.numPendingScenarios, "pending.html", "Pending Scenarios" );
        writeLink( totalStatistics.numFailedScenarios, "failed.html", "Failed Scenarios" );
        writer.println( "</ul>" );
    }

    private void writeLink( int count, String fileName, String title ) {
        if( count > 0 ) {
            writer.print( "<li><a href='" + fileName + "'>" + title );
            writer.println( " <span class='badge count'>" + count + "</span></a>" );
        }
    }

    private void writeSearchInput() {
        writer.println( "<input class='search-input' id='toc-search-input' "
                + "placeholder='enter regexp to search in toc' onkeydown='searchChanged(event)'></input>" );
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
                String collapsed = "collapsed";
                writer.println( "<ul class='" + collapsed + "' id='" + toc.name + "'>" );
            }
            printPackageTocs( toc.packages );
            printClassLinks( toc.files );
            if( !toc.name.equals( "" ) ) {
                writer.print( "</ul></li>" );
            }
        }
    }

    private void printClassLinks( List<ReportModelFile> files ) {
        for( ReportModelFile modelFile : files ) {
            writeClassLink( modelFile );
        }
    }

    private void writeClassLink( ReportModelFile model ) {
        writer.print( format( "<li><a href='%s' >%s</a></li>",
            model.file.getName(),
            model.model.getSimpleClassName() ) );
    }

    private void writeTagLinks() {
        if( groupedTags.isEmpty() ) {
            return;
        }

        writer.println( "<h3>Tags</h3>" );
        writer.println( "<ul>" );
        List<String> orderedKeys = Ordering.natural().sortedCopy( groupedTags.keySet() );
        for( String key : orderedKeys ) {
            writer.println( "<li>" );
            String tagId = "tag" + key;
            writer.println( "<h4 onclick='toggle(\"" + tagId + "\")'>" + key + "</h4>" );
            writer.println( "<ul id='" + tagId + "' class='collapsed'>" );

            List<Tag> sortedTags = Ordering.usingToString().sortedCopy( groupedTags.get( key ) );
            for( Tag tag : sortedTags ) {
                writeTagLink( tag, reportModel.getScenariosByTag( tag ) );
            }
            writer.println( "</ul>" );
        }
        writer.println( "</ul>" );
    }

    private ImmutableListMultimap<String, Tag> getGroupedTags() {
        ImmutableListMultimap<String, Tag> multiMap = Multimaps.index( reportModel.getAllTags(), new Function<Tag, String>() {
            @Override
            public String apply( Tag input ) {
                return input.getName();
            }
        } );
        return multiMap;
    }

    public List<Tag> getSortedTags() {
        List<Tag> sortedTags = Lists.newArrayList( reportModel.getAllTags() );
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
        return tag.toEscapedString() + ".html";
    }

}
