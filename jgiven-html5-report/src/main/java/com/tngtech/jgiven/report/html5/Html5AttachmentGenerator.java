package com.tngtech.jgiven.report.html5;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.io.BaseEncoding;
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
    public static final int MINIMAL_THUMBNAIL_SIZE = 20;

    private File attachmentsDir;
    private String subDir;
    private Multiset<String> fileCounter = HashMultiset.create();
    private Set<String> usedFileNames = Sets.newHashSet();
    private String htmlSubDir;

    public Html5AttachmentGenerator() {
    }

    @VisibleForTesting
    public Html5AttachmentGenerator( File attachmentsDir ) {
        this.attachmentsDir = attachmentsDir;
    }

    public void generateAttachments( File targetDir, ReportModel model ) {
        subDir = ATTACHMENT_DIRNAME + File.separatorChar + model.getClassName().replace( '.', File.separatorChar );
        htmlSubDir = subDir.replace( File.separatorChar, '/' );
        attachmentsDir = new File( targetDir, subDir );

        if( !attachmentsDir.exists() && !attachmentsDir.mkdirs() ) {
            throw new JGivenInstallationException( "Could not create directory " + attachmentsDir );
        }
        model.accept( this );
    }

    @Override
    public void visit( StepModel stepModel ) {
        List<AttachmentModel> attachments = stepModel.getAttachments();
        for( AttachmentModel attachment : attachments ) {
            writeAttachment( attachment );
        }
    }

    private void writeAttachment( AttachmentModel attachment ) {
        String mimeType = attachment.getMediaType();
        MediaType mediaType = MediaType.parse( mimeType );
        File targetFile = writeFile( attachment, mediaType );
        attachment.setValue( htmlSubDir + "/" + targetFile.getName() );
        log.debug( "Attachment written to " + targetFile );
    }

    private String getExtension( MediaType mediaType ) {
        if( mediaType.is( MediaType.SVG_UTF_8 ) ) {
            return "svg";
        }

        if( mediaType.is( MediaType.ICO ) ) {
            return "ico";
        }

        if( mediaType.is( MediaType.BMP ) ) {
            return "bmp";
        }

        return mediaType.subtype();
    }

    File getTargetFile( String fileName, String extension ) {
        if( fileName == null ) {
            fileName = "attachment";
        }

        int count = fileCounter.count( fileName );
        fileCounter.add( fileName );

        String suffix = "";
        if( count > 0 ) {
            count += 1;
            suffix = String.valueOf( count );
        }

        String fileNameWithExtension = fileName + suffix + "." + extension;

        while( usedFileNames.contains( fileNameWithExtension ) ) {
            fileCounter.add( fileName );
            count++;
            suffix = String.valueOf( count );
            fileNameWithExtension = fileName + suffix + "." + extension;
        }
        usedFileNames.add( fileNameWithExtension );
        return new File( attachmentsDir, fileNameWithExtension );
    }

    private File getThumbnailFileFor( File originalImage ) {
        String orgName = originalImage.getName();
        String newName = "";

        int dotIndex = orgName.lastIndexOf( "." );

        if( dotIndex == -1 ) {
            newName = orgName + "-thumb";
        } else {
            String extension = orgName.subSequence( dotIndex + 1, orgName.length() ).toString();
            newName = orgName.substring( 0, dotIndex ) + "-thumb." + extension;
        }
        return new File( attachmentsDir, newName );

    }

    private File writeFile( AttachmentModel attachment, MediaType mediaType ) {
        String extension = getExtension( mediaType );
        File targetFile = getTargetFile( attachment.getFileName(), extension );
        try {
            if( attachment.isBinary() ) {
                if( mediaType.is( MediaType.ANY_IMAGE_TYPE ) ) {
                    File thumbFile = getThumbnailFileFor( targetFile );
                    byte[] thumbnail = compressToThumbnail( attachment.getValue(), extension );
                    Files.write( thumbnail, thumbFile );
                }
                Files.write( BaseEncoding.base64().decode( attachment.getValue() ), targetFile );
            } else {
                Files.write( attachment.getValue(), targetFile, Charsets.UTF_8 );
            }
        } catch( IOException e ) {
            log.error( "Error while trying to write attachment to file " + targetFile, e );
        }
        return targetFile;
    }

    private byte[] compressToThumbnail( String base64content, String extension ) {
        byte[] imageBytes = BaseEncoding.base64().decode( base64content );
        double scaleFactor = 0.02;
        byte[] base64thumb = {};
        try {
            BufferedImage before = ImageIO.read( new ByteArrayInputStream( imageBytes ) );
            BufferedImage after = scaleBy( scaleFactor, before );
            base64thumb = bufferedImageToBase64( after, extension );
        } catch( IOException e ) {
            log.error( "Error while decoding the attachment to BufferedImage ", e );
        }
        return base64thumb;
    }

    private BufferedImage scaleBy( double factor, BufferedImage before ) {
        int width = Math.max( (int) Math.round( before.getWidth() * factor ), MINIMAL_THUMBNAIL_SIZE );
        int height = Math.max( (int) Math.round( before.getHeight() * factor ), MINIMAL_THUMBNAIL_SIZE );
        BufferedImage after = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        AffineTransform at = new AffineTransform();
        at.scale( factor, factor );
        AffineTransformOp scaleOp = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );

        return scaleOp.filter( before, after );
    }

    private byte[] bufferedImageToBase64( BufferedImage bi, String extension ) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageArray = {};
        try {
            ImageIO.write( bi, extension, baos );
            imageArray = baos.toByteArray();
            baos.close();
        } catch( IOException e ) {
            log.error( "Error while decoding the compressed BufferedImage to base64 ", e );
        }
        return imageArray;
    }
}
