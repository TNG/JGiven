package com.tngtech.jgiven.report.text;

import java.io.PrintWriter;

import com.tngtech.jgiven.relocated.jansi.Ansi.*;
import com.tngtech.jgiven.relocated.jansi.*;
import com.tngtech.jgiven.report.model.ReportModelVisitor;

import static com.tngtech.jgiven.relocated.jansi.Ansi.ansi;

public class PlainTextWriter extends ReportModelVisitor {
    protected final PrintWriter writer;
    protected final boolean withColor;

    public PlainTextWriter( PrintWriter printWriter, boolean withColor ) {
        this.writer = printWriter;
        this.withColor = withColor;
    }

    void println(Color color, String text ) {
        writer.println( withColor( color, text ) );
    }

    String withColor(Color color, String text ) {
        return withColor( color, false, null, text );
    }

    String gray( String text ) {
        return withColor( Color.BLACK, true, null, text );
    }

    String green( String text ) {
        return withColor( Color.GREEN, text );
    }

    String boldGray( String text ) {
        return withColor( Color.BLACK, true, Attribute.INTENSITY_BOLD, text );
    }

    String boldRed( String text ) {
        return withColor( Color.RED, false, Attribute.INTENSITY_BOLD, text );
    }

    String bold( String text ) {
        if( withColor ) {
            return ansi().bold().a( text ).boldOff().toString();
        }
        return text;
    }

    String withColor(Color color, Attribute attribute, String text ) {
        return withColor( color, false, attribute, text );
    }

    String withColor(Color color, boolean bright, Attribute attribute, String text ) {
        if( withColor ) {
            Ansi ansi = bright ? ansi().fgBright( color ) : ansi().fg( color );
            if( attribute != null ) {
                ansi = ansi.a( attribute );
            }
            return ansi.a( text ).reset().toString();
        }
        return text;
    }
}
