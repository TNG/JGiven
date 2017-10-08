package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.BaseEncoding;

public class Html5AttachmentGeneratorTest {

    private static final String JSON_SAMPLE = "{}";
    private static final byte[] BINARY_SAMPLE = BaseEncoding.base64().decode( "89504E470D0A1A0A" );

    @Rule
    public final TemporaryFolder temporaryFolderRule = new TemporaryFolder();

    @Test
    public void testFileNameGeneration() {
        Html5AttachmentGenerator generator = new Html5AttachmentGenerator();

        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo.txt" );
        assertThat( generator.getTargetFile( "foo", "txt" ).getName() ).isEqualTo( "foo2.txt" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo3.png" );
        assertThat( generator.getTargetFile( "foo4", "png" ).getName() ).isEqualTo( "foo4.png" );
        assertThat( generator.getTargetFile( "foo", "png" ).getName() ).isEqualTo( "foo5.png" );

    }

}