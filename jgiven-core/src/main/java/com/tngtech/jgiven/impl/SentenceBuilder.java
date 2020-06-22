package com.tngtech.jgiven.impl;

import com.google.common.collect.ImmutableList;
import com.tngtech.jgiven.report.model.Word;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class SentenceBuilder {

    private final Deque<Word> words = new ArrayDeque<>();

    private boolean joinNextWord;

    boolean hasWords() {
        return !words.isEmpty();
    }

    void clear() {
        words.clear();
    }

    List<Word> getWords() {
        return ImmutableList.copyOf( words );
    }

    void addIntroWord( String value ) {
        words.removeIf( Word::isIntroWord );
        words.addFirst( new Word( value, true ) );
    }

    void addWord( Word word ) {
        addWord( word, false, false );
    }

    void addWord( String value, boolean joinToPreviousWord, boolean joinToNextWord ) {
        addWord( new Word( value ), joinToPreviousWord, joinToNextWord );
    }

    void addWord( Word word, boolean joinToPreviousWord, boolean joinToNextWord ) {
        if ( hasWords() && joinNextWord ) {
            word.addPrefix( words.removeLast().getFormattedValue() );
            words.add( word );
        } else if ( hasWords() && joinToPreviousWord ) {
            words.getLast().addSuffix( word.getFormattedValue() );
        } else {
            words.add( word );
        }

        joinNextWord = joinToNextWord;
    }

}
