package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.attachment.Attachment;

public class StepModel {
    /**
     * The original name of this step as it appeared in the Java code.
     */
    public String name;

    /**
     * All words of this step including the introduction word.
     */
    public List<Word> words = Lists.newArrayList();

    /**
     * The execution status of this step.
     */
    private StepStatus status = StepStatus.PASSED;

    /**
     * The total execution time of the step in nano seconds.
     */
    private long durationInNanos;

    /**
     * An optional extended description of this step.
     * Can be {@code null}
     */
    private String extendedDescription;

    /**
     * An optional attachment of the step
     */
    private AttachmentModel attachment;

    public StepModel() {}

    public StepModel( String name, List<Word> words ) {
        this.name = name;
        this.words = Lists.newArrayList( words );
    }

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
    }

    public String getCompleteSentence() {
        return Joiner.on( ' ' ).join( words );
    }

    public StepModel addWords( Word... words ) {
        this.words.addAll( Arrays.asList( words ) );
        return this;
    }

    public boolean isNotImplementedYet() {
        return getStatus() == StepStatus.NOT_IMPLEMENTED_YET;
    }

    public boolean isFailed() {
        return getStatus() == StepStatus.FAILED;
    }

    public boolean isSkipped() {
        return getStatus() == StepStatus.SKIPPED;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus( StepStatus status ) {
        this.status = status;
    }

    public long getDurationInNanos() {
        return durationInNanos;
    }

    public void setDurationInNanos( long durationInNanos ) {
        this.durationInNanos = durationInNanos;
    }

    public Word getWord( int i ) {
        return this.words.get( i );
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public boolean hasExtendedDescription() {
        return extendedDescription != null;
    }

    public void setExtendedDescription( String extendedDescription ) {
        this.extendedDescription = extendedDescription;
    }

    public Iterable<Word> getWords() {
        return Collections.unmodifiableList( words );
    }

    public Word getLastWord() {
        return words.get( words.size() - 1 );
    }

    public void setAttachment( Attachment attachment ) {
        this.attachment = new AttachmentModel();
        this.attachment.setValue( attachment.asString() );
        this.attachment.setMimeType( attachment.getMimeType() );
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }
}