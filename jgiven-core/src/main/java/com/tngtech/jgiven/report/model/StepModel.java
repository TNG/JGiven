package com.tngtech.jgiven.report.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tngtech.jgiven.attachment.Attachment;

public class StepModel {
    /**
     * The original name of this step as it appeared in the Java code.
     */
    private String name;

    /**
     * All words of this step including the introduction word.
     */
    private List<Word> words = Lists.newArrayList();

    /**
     * An optional list of nested steps
     * Can be {@code null}
     */
    private List<StepModel> nestedSteps;

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

    /**
     * Whether this step is a section title.
     * Section titles look differently in the generated report.
     * Can be {@code null} which is equivalent to {@code false}
     * 
     * @since 0.10.2
     */
    private Boolean isSectionTitle;

    public StepModel() {}

    public StepModel( String name, List<Word> words ) {
        this.setName( name );
        this.setWords( Lists.newArrayList( words ) );
    }

    public void accept( ReportModelVisitor visitor ) {
        visitor.visit( this );
    }

    public String getCompleteSentence() {
        return Joiner.on( ' ' ).join( this.words );
    }

    public StepModel addWords( Word... words ) {
        this.words.addAll( Arrays.asList( words ) );
        return this;
    }

    public boolean isPending() {
        return getStatus() == StepStatus.PENDING;
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
        return words.get( i );
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public boolean hasExtendedDescription() {
        return extendedDescription != null || Iterables.size( nestedSteps ) > 0;
    }

    public void setExtendedDescription( String extendedDescription ) {
        this.extendedDescription = extendedDescription;
    }

    public List<Word> getWords() {
        return Collections.unmodifiableList( words );
    }

    public Word getLastWord() {
        return this.words.get( this.words.size() - 1 );
    }

    public void setAttachment( Attachment attachment ) {
        this.attachment = new AttachmentModel();
        this.attachment.setTitle( attachment.getTitle() );
        this.attachment.setValue( attachment.getContent() );
        this.attachment.setFileName( attachment.getFileName() );
        this.attachment.setMediaType( attachment.getMediaType().asString() );
        this.attachment.setIsBinary( attachment.getMediaType().isBinary() );
        this.attachment.setShowDirectly( attachment.getShowDirectly() );
    }

    public AttachmentModel getAttachment() {
        return attachment;
    }

    public void addNestedStep( StepModel stepModel ) {
        if( nestedSteps == null ) {
            nestedSteps = Lists.newArrayList();
        }
        nestedSteps.add( stepModel );
    }

    public List<StepModel> getNestedSteps() {
        if( nestedSteps != null ) {
            return nestedSteps;
        }
        return Collections.emptyList();
    }

    public void setNestedSteps( List<StepModel> nestedSteps ) {
        this.nestedSteps = nestedSteps;
    }

    public Boolean isSectionTitle() {
        return isSectionTitle == null ? false : isSectionTitle;
    }

    public void setIsSectionTitle( boolean isSectionTitle ) {
        this.isSectionTitle = isSectionTitle ? true : null;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setWords( List<Word> words ) {
        this.words = words;
    }

    public void addIntroWord( Word introWord ) {
        words.add( 0, introWord );
    }
}