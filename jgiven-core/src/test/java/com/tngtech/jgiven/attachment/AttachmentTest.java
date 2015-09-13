package com.tngtech.jgiven.attachment;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class AttachmentTest {

    private static final byte[] ARBITRARY_BYTES = new byte[] { 3, 4, 12, 23 };
    public static final MediaType ARBITRARY_MEDIA_TYPE = MediaType.binary( MediaType.Type.APPLICATION, "unknown" );
    public static final String HELLO_JGIVEN = "Hello JGiven";

    @Rule
    public TemporaryFolder tmpFolderRule = new TemporaryFolder();
    private File binaryFile;
    private File textFile;

    @Before
    public void createTestFiles() throws IOException {
        File tmpFolder = tmpFolderRule.newFolder();
        binaryFile = new File( tmpFolder, "binaryfile" );
        Files.write( ARBITRARY_BYTES, binaryFile );
        textFile = new File( tmpFolder, "file.txt" );
        Files.write( HELLO_JGIVEN, textFile, Charsets.UTF_8 );
    }

    @Test
    public void testBinaryConverter() throws IOException {
        Attachment attachment = Attachment.fromBinaryFile( binaryFile, ARBITRARY_MEDIA_TYPE );
        assertThat( attachment.getContent() ).isEqualTo( "AwQMFw==" );

        attachment = Attachment.fromBinaryBytes( ARBITRARY_BYTES, ARBITRARY_MEDIA_TYPE );
        assertThat( attachment.getContent() ).isEqualTo( "AwQMFw==" );
    }

    @Test
    public void testTextConverter() throws IOException {
        assertThat( Attachment.fromTextFile( textFile, MediaType.PLAIN_TEXT_UTF_8 ).getContent() ).isEqualTo( HELLO_JGIVEN );
        assertThat( Attachment.xml( HELLO_JGIVEN ).getContent() ).isEqualTo( HELLO_JGIVEN );
        assertThat( Attachment.json( HELLO_JGIVEN ).getContent() ).isEqualTo( HELLO_JGIVEN );
    }
}
