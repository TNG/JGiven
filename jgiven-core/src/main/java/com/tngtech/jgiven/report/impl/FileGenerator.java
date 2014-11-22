package com.tngtech.jgiven.report.impl;

import java.io.File;
import java.io.IOException;

public interface FileGenerator {
    void generate( File toDir, File fromDir ) throws IOException;
}
