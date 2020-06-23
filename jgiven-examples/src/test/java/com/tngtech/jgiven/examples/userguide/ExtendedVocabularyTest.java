package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.As;
import com.tngtech.jgiven.annotation.FillerWord;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

import static com.tngtech.jgiven.examples.userguide.ExtendedVocabularyTest.Steps;

public class ExtendedVocabularyTest extends SimpleScenarioTest<Steps> {

    @Test
    public void with_filler_words() {

        // tag::givenFiller[]

        given().the().ingredients()
            .an().egg()
            .some().milk()
            .and().the().ingredient( "flour" );

        // end::givenFiller[]

    }

    @Test
    public void with_joining_words() {

        // tag::givenJoiningWords[]

        given().a().open_bracket().clean().close_bracket().worksurface().comma().a().bowl().and().the().ingredients().colon()
            .an().egg()
            .some().milk()
            .the().ingredient( "flour" );

        // end::givenJoiningWords[]

    }


    public static class ExtendedVocabularyStage<SELF extends ExtendedVocabularyStage<?>> extends Stage<SELF> {

        // tag::fillerWords[]

        @FillerWord
        public SELF a() {
            return self();
        }


        @FillerWord
        public SELF an() {
            return self();
        }

        @FillerWord
        public SELF and() {
            return self();
        }

        @FillerWord
        public SELF some() {
            return self();
        }

        @FillerWord
        public SELF the() {
            return self();
        }

        // end::fillerWords[]

        // tag::joiningWords[]

        @As(",")
        @FillerWord(joinToPreviousWord = true)
        public SELF comma() {
            return self();
        }

        @As(":")
        @FillerWord(joinToPreviousWord = true)
        public SELF colon() {
            return self();
        }

        @As("(")
        @FillerWord(joinToNextWord = true)
        public SELF open_bracket() {
            return self();
        }

        @As(")")
        @FillerWord(joinToPreviousWord = true)
        public SELF close_bracket() {
            return self();
        }

        // end::joiningWords[]

    }

    public static class Steps extends ExtendedVocabularyStage<Steps> {

        public Steps ingredients() {
            return self();
        }

        @FillerWord
        public Steps clean() {
            return self();
        }

        @FillerWord
        public Steps worksurface() {
            return self();
        }

        @FillerWord
        public Steps bowl() {
            return self();
        }

        public Steps egg() {
            return self();
        }

        public Steps milk() {
            return self();
        }

        public Steps ingredient( String ingredient ) {
            return self();
        }
    }

}
