package com.tngtech.jgiven.report.model;

import java.util.List;

import com.beust.jcommander.internal.Lists;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;

public class GivenAttachments<SELF extends GivenAttachments<?>> extends Stage<SELF> {

    @ProvidedScenarioState
    List<Attachment> attachments = Lists.newArrayList();

    public SELF an_attachment_with_content_$_and_mediaType(@Quoted String content, @Quoted MediaType mediaType ) {
        return an_attachment( Attachment.fromText( content, mediaType ) );
    }

    public SELF an_attachment_with_binary_content_$_and_mediaType(@Quoted String binaryContent, @Quoted MediaType mediaType) {
        return an_attachment(Attachment.fromBase64(binaryContent,mediaType));
    }

    public SELF an_attachment(Attachment attachment) {
        attachments.add(attachment);
        return self();
    }


    public SELF file_name( String name ) {
        getLastAttachment().withFileName( name );
        return self();
    }

    public Attachment getLastAttachment() {
        return attachments.get( attachments.size() - 1 );
    }

}
