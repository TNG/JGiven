package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * Marks a step method to have nested steps.
 * <p>
 *     Nested steps are useful if you want to group certain steps together under a new description.
 *     This allows you to give a larger scenario more structure and make it better readable
 *
 * </p>
 * <h2>Example</h2>
 * Let's say you want to express that the registration form is filled with valid values, then you
 * can do this as follows:
 *
 * <pre>
 * {@literal @}NestedSteps
 * public NestedStage I_fill_out_the_registration_form_with_valid_values() {
 *    return I_enter_a_name( "Franky" )
 *       .and().I_enter_a_password( "password1234" )
 *       .and().I_enter_a_repeated_password( "password1234" );
 * }
 * </pre>
 *
 * The resulting console report will look as follows:
 * <pre>
 *    Given I fill out the registration form with valid values
 *            I enter a name Franky
 *            And I enter a email address franky@acme.com
 *            And I enter a password password1234
 *            And I enter a repeated password password1234
 * </pre>
 * 
 * In the HTML report nested steps can be expanded and collapsed
 * @since 0.10.0
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface NestedSteps {}
