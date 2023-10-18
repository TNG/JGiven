package com.tngtech.jgiven.impl.util;

import com.google.common.base.Charsets;
import com.tngtech.jgiven.config.ConfigValue;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class PrintWriterUtil {
    public static PrintWriter getPrintWriter(File file) {
        try {
            return new PrintWriter(file, Charsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PrintWriter getPrintWriter(OutputStream outputStream, ConfigValue colorConfig) {
        OutputStream wrappedStream = outputStream;

        if (colorConfig == ConfigValue.TRUE || colorConfig == ConfigValue.AUTO) {
            AnsiConsole.systemInstall();
            wrappedStream = AnsiUtil.wrapOutputStream(outputStream, colorConfig == ConfigValue.AUTO);
        }

        return new PrintWriter(new OutputStreamWriter(wrappedStream, Charsets.UTF_8));
    }

}
