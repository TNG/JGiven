package com.tngtech.jgiven.report.html;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.util.Formatter;
import java.util.Locale;

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
        // TODO: provide a configuration value to configure the locale
        double durationInMs = ( (double) durationInNanos ) / 1000000;
        Formatter usFormatter = new Formatter( Locale.US );
        writer.print( usFormatter.format( " <span class='duration'>(%.2f ms)</span>", durationInMs ) );
        usFormatter.close();
    }

}
