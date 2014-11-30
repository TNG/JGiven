package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;

import com.tngtech.jgiven.report.util.DurationFormatter;

public class HtmlWriterUtils {

    private final PrintWriter writer;

    public HtmlWriterUtils( PrintWriter writer ) {
        this.writer = writer;
    }

    public void writeHtmlHeader( String title ) {
        writer.println( "<!DOCTYPE html>" );
        writer.println( "<html><head>" );
        writer.println( "  <meta charset='utf-8'>" );
        writer.println( format( "  <title>%s</title>", title ) );
        writer.println( "  <link href='style.css' rel='stylesheet'>" );
        writer.println( "</head>" );
        writer.println( "<body>" );
    }

    public void writeHtmlFooter() {
        writer.println( "</body>" );
        writer.println( "</html>" );
    }

    public void writeDuration( long durationInNanos ) {
        writer.print( " <span class='duration'>(" + DurationFormatter.format( durationInNanos ) + ")</span>" );
    }

}
