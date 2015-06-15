package com.tngtech.jgiven.examples.attachments;

import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.attachment.Attachment;

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
}
