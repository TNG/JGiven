package com.tngtech.jgiven.report.html5;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.report.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;

public class Html5AttachmentGeneratorTest {

    private static final String JSON_SAMPLE = "{}";
    private static final byte[] BINARY_SAMPLE = DatatypeConverter.parseHexBinary( "89504E470D0A1A0A" );

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

    @Test
    public void testFileCreation() throws IOException {
        // given
        ScenarioModel scenarioModel = new ScenarioModel();
        ScenarioCaseModel case1 = new ScenarioCaseModel();
        StepModel step1 = new StepModel();
        step1.addAttachment( Attachment.fromText( JSON_SAMPLE, MediaType.JSON_UTF_8 ).withFileName( "json" ) );
        step1.addAttachment( Attachment.fromBinaryBytes( BINARY_SAMPLE, MediaType.PNG ).withFileName( "png" ) );
        step1.setStatus( StepStatus.PASSED );
        case1.addStep( step1 );
        case1.setSuccess( false );
        scenarioModel.addCase( case1 );
        ReportModel reportModel = new ReportModel();
        reportModel.addScenarioModel( scenarioModel );
        reportModel.setClassName( "report" );
        File attachmentDir = temporaryFolderRule.newFolder( "attachments" );
        File reportDir = new File( attachmentDir, "report" );
        File json = new File( reportDir, "json.json" );
        json.delete();
        File png = new File( reportDir, "png.png" );
        png.delete();

        // when
        new Html5AttachmentGenerator().generateAttachments( temporaryFolderRule.getRoot(), reportModel );

        // then
        assertThat( json ).exists();
        assertThat( json ).hasContent( JSON_SAMPLE );
        assertThat( png ).exists();
        assertThat( png ).hasBinaryContent( BINARY_SAMPLE );
    }

}