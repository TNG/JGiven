package com.tngtech.jgiven.report.html5;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.tngtech.jgiven.exception.JGivenInstallationException;
import com.tngtech.jgiven.report.model.AttachmentModel;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ReportModelVisitor;
import com.tngtech.jgiven.report.model.StepModel;

class Html5AttachmentGenerator extends ReportModelVisitor {
    private static final Logger log = LoggerFactory.getLogger( Html5AttachmentGenerator.class );
    private static final String ATTACHMENT_DIRNAME = "attachments";

    private int fileCounter;
    private File attachmentsDir;

    public void generateAttachments( File targetDir, ReportModel model ) {
        attachmentsDir = new File( targetDir, ATTACHMENT_DIRNAME );
        if( !attachmentsDir.exists() && !attachmentsDir.mkdirs() ) {
            throw new JGivenInstallationException( "Could not create directory " + attachmentsDir );
        }
        model.accept( this );
    }

    @Override
    public void visit( StepModel stepModel ) {
        AttachmentModel attachment = stepModel.getAttachment();
        if( attachment == null ) {
            return;
        }

        String mimeType = attachment.getMediaType();
        MediaType mediaType = MediaType.parse( mimeType );
        File targetFile = null;
        if( mediaType.is( MediaType.ANY_TEXT_TYPE ) ) {
            targetFile = writeTextFile( attachment );
        } else if( mediaType.is( MediaType.ANY_IMAGE_TYPE ) ) {
            targetFile = writeImageFile( attachment, mediaType );
        }

        if( targetFile != null ) {
            attachment.setValue( ATTACHMENT_DIRNAME + "/" + targetFile.getName() );
        } else {
            attachment.setValue( null );
        }
        log.info( "Attachment written to " + targetFile );
    }

    private File writeImageFile( AttachmentModel attachment, MediaType mediaType ) {
        if( !mediaType.is( MediaType.PNG ) ) {
            log.error( "Mime type " + mediaType + " is not supported as an image attachment. Only PNG is supported." );
        }

        String extension = "png";
        File targetFile = getTargetFile( extension );
        try {
            Files.write( parseBase64Binary( attachment.getValue() ), targetFile );
        } catch( IOException e ) {
            log.error( "Error while trying to write attachment to file " + targetFile, e );
        }
        return targetFile;
    }

    private File getTargetFile( String extension ) {
        return new File( attachmentsDir, "attachment" + getNextFileCounter() + "." + extension );
    }

    private File writeTextFile( AttachmentModel attachment ) {
        File targetFile = getTargetFile( "txt" );
        try {
            Files.write( attachment.getValue(), targetFile, Charsets.UTF_8 );
        } catch( IOException e ) {
            log.error( "Error while trying to write attachment to file " + targetFile, e );
        }
        return targetFile;
    }

    private int getNextFileCounter() {
        return fileCounter++;
    }
}
