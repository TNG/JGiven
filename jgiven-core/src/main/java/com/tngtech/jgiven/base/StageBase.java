package com.tngtech.jgiven.base;

/**
 * Useful base class for step definitions as it provides a {@link #self()} method
 * to create fluent interfaces.
 * <p>
 * Direct subclasses should provide introduction words (see {@link com.tngtech.jgiven.annotation.IntroWord}).
 * <p>
 * Typically one derives from one of the language-specific step definition classes, which
 * already provide a handful of useful introduction words.
 *
 * @param <SELF> the type of the subclass
 * @see com.tngtech.jgiven.Stage
 * @see com.tngtech.jgiven.lang.de.Stufe
 */
public class StageBase<SELF extends StageBase<?>> {

    @SuppressWarnings( "unchecked" )
    public SELF self() {
        return (SELF) this;
    }

}
