package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

import com.tngtech.jgiven.format.ArgumentFormatter;
import com.tngtech.jgiven.format.PrintfFormatter;

/**
 * Marks the parameter of a step method as a data table.
 * Such parameters are represented as tables in the report.
 * <p>
 * Only parameters that implement {@link java.lang.Iterable} or arrays can be treated as data tables.
 * The elements can either be again {@link java.lang.Iterable} instances the data for each row
 * of the table. Note, that in that case the first element is taken as the header of the table.
 * <p>
 * Elements can also be plain POJOs, in which case the field names become the headers and field values the data.
 * <p>
 * <h3>Example</h3>
 * <h4>Some POJO</h4>
 * <pre>{@code
 *     class CoffeeWithPrice {
 *         String name;
 *         double price_in_EUR;
 *         CoffeeWithPrice(String name, double priceInEur) {
 *            this.name = name;
 *            this.price_in_EUR = priceInEur;
 *         }
 *     }
 * }</pre>
 * <h4>The Step Method</h4>
 * <pre>{@code
 *     public SELF the_prices_of_the_coffees_are( @Table CoffeeWithPrice... prices ) {
 *         ...
 *     }
 * }</pre>
 * <h4>Invocation of the step method</h4>
 * <pre>{@code
 *     given().the_prices_of_the_coffees_are(
 *         new CoffeeWithPrice("Espresso", 2.0),
 *         new CoffeeWithPrice("Cappuccino", 2.5));
 * }</pre>
 * <h4>Text Report</h4>
 * <pre>{@code
 *     Given the prices of the coffees are
 *     
 *          | name       | price in EUR |
 *          +------------+--------------+
 *          | Espresso   | 2.0          |
 *          | Cappuccino | 2.5          |
 * 
 * 
 * }</pre>
 *
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Table {
    Class<? extends ArgumentFormatter<?>> value() default PrintfFormatter.class;

    /**
     * Optional arguments for the ArgumentFormatter.
     */
    String[] args() default {};
}
