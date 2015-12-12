package com.tngtech.jgiven.base;

import com.tngtech.jgiven.StepFunction;
import com.tngtech.jgiven.annotation.Hidden;

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

    @Hidden
    @SuppressWarnings( "unchecked" )
    public SELF self() {
        return (SELF) this;
    }

    /**
     * A step method for creating ad-hoc steps using lambdas.
     *
     * <h2>Example Usage</h2>
     * <pre>{@code 
     *     given().$( "Two negative arguments", stage -> {
     *        stage.given().argument( -5 )
     *             .and().argument( -6 );
     *     });
     * }</pre>
     *
     * @param description the description of the step
     * @param function the implementation of the step in form of a function where the parameter is the stage the step is executed in
     * @since 0.7.1
     */
    public SELF $( String description, @Hidden StepFunction<? super SELF> function ) throws Exception {
        function.apply( self() );
        return self();
    }
}
