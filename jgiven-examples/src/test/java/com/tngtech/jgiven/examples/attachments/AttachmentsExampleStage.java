package com.tngtech.jgiven.examples.attachments;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AttachmentsExampleStage extends Stage<AttachmentsExampleStage> {

    @ExpectedScenarioState
    CurrentStep currentStep;

    private String content;

    public void some_text_content( @Quoted String content ) {
        this.content = content;
    }

    public void it_can_be_added_as_an_attachment_to_the_step_with_title( String title ) {
        currentStep.addAttachment( Attachment.plainText( content )
                .withTitle( title ) );
    }

    public void it_can_be_added_as_an_attachment_multiple_times_to_the_step() {
        currentStep.addAttachment( Attachment.plainText( content ).withTitle( "First Attachment" ) );
        currentStep.addAttachment( Attachment.plainText( content ).withTitle( "Second Attachment" ) );
    }

    public void a_large_oval_circle() throws IOException {
        addOvalAttachment( 800, 600, Color.BLUE, "large-oval-circle" );
    }

    public void an_oval_circle() throws IOException {
        addOvalAttachment( 300, 200, Color.BLUE, "oval-circle" );
    }

    public void a_$_oval_circle( String color ) throws IOException {
        addOvalAttachment( 300, 200, getColor( color ), "oval-circle" );
    }

    public void an_oval_circle_as_thumbnail() throws IOException {
        byte[] bytes = drawOval(300, 200, Color.BLUE);

        currentStep.addAttachment(
                Attachment.fromBinaryBytes( bytes, MediaType.PNG )
                        .withTitle( "An oval drawn in Java" )
                        .withFileName( "oval-circle-as-thumbnail" ));
    }

    private Color getColor( String color ) {
        if( color.equals( "red" ) ) {
            return Color.RED;
        }
        return Color.BLUE;
    }

    private void addOvalAttachment(int width, int height, Color color, String fileName ) throws IOException {
        byte[] bytes = drawOval(width, height, color);

        currentStep.addAttachment(
                Attachment.fromBinaryBytes( bytes, MediaType.PNG )
                        .withTitle( "An oval drawn in Java" )
                        .withFileName( fileName )
                        .showDirectly() );
    }

    private byte[] drawOval(int width, int height, Color color) throws IOException {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );

        Graphics2D g = image.createGraphics();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

        g.setStroke( new BasicStroke( 10 ) );
        g.setPaint( color );
        g.drawOval( 10, 10, width - 20, height - 20 );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        try {
            ImageIO.write( image, "PNG", outputStream );
            bytes = outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
        return bytes;
    }
}
