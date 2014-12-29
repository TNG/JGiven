package com.tngtech.jgiven.format;

/**
 * Translates <code>false</code> to the word "not" and <code>true</code> to the empty word "".
 *
 * <pre>
 * true:  ""
 * false: "not"
 * </pre>
 *
 * <h1>Example:</h1>
 * <pre>
 * then().the_coffee_should_$_be_served( coffeeServed )
 * </pre>
 * <h2>Result:</h2>
 * <h3>coffeeServed == false</h3>
 * <pre>
 * then the coffee should not be served
 * </pre>
 * <h3>coffeeServed == true</h3>
 * <pre>
 * then the coffee should be served
 * </pre>
 */
public class NotFormatter extends BooleanFormatter {

    @Override
    public String format( Boolean b, String... args ) {
        return super.format( b, "", "not" );
    }
}
