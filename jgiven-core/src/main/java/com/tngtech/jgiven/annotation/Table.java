package com.tngtech.jgiven.annotation;

import static com.tngtech.jgiven.annotation.Table.HeaderType.HORIZONTAL;

import java.lang.annotation.*;

/**
 * Marks the parameter of a step method as a data table.
 * Such parameters are represented as tables in the report.
 * <p>
 * Only parameters that implement {@link java.lang.Iterable} or arrays can be treated as data tables.
 * The elements can either be again {@link java.lang.Iterable} instances the data for each row
 * of the table.
 * <p>
 * Note, that in that case the first list is taken as the header of the table if the {@link Table#columnTitles()} are not set.
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
 * @since 0.6.1
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
public @interface Table {
    /**
     * Specifies the header type of the table. Default is {@code HORIZONTAL}.
     * <p>
     * That is explained best by an example. <br>
     * Given the following table argument:
     * <pre>
     * {@code new Object[][] {
     *     { "a1", "a2", "a3" },
     *     { "b1", "b2", "b3" },
     *     { "c1", "c2", "c3" }}
     * }
     * </pre>
     * Then the header type argument has the following effect.
     * <h3>{@code HeaderType.NONE}</h3>
     * This simply specifies the the table has no header. The plain text report will produce the following output.
     * <pre>
     *     | a1 | a2 | a3 |
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     * <h3>{@code HeaderType.HORIZONTAL}</h3>
     * Specifies that the first <em>row</em> represents the header.
     * <pre>
     *     | a1 | a2 | a3 |
     *     +----+----+----+
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     * <h3>{@code HeaderType.VERTICAL}</h3>
     * Specifies that the first <em>column</em> represents the header. Thus elements a1, b1, and c1.
     * The plain text report will produce the same output as for header type NONE, however, the HTML report will
     * render the first column as a header.
     * <pre>
     *     | a1 | a2 | a3 |
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     * <h3>{@code HeaderType.BOTH}</h3>
     * Specifies that the first <em>row</em> and the first <em>column</em> are headers.
     * The plain text report will produce the same output as for header type HORIZONTAL, however, the HTML report will
     * render the first row and the first column as headers.
     * <pre>
     *     | a1 | a2 | a3 |
     *     +----+----+----+
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     * </p>
     *
     * <h2>Effect on POJO lists</h2>
     * When the data is given by a list of POJOs then setting the header type to {@code VERTICAL} will also
     * <em>transpose</em> the table. For example
     * <p>
     * Given the following POJO list.
     * <pre>{@code new CoffeeWithPrice[] {
     *         new CoffeeWithPrice("Espresso", 2.0),
     *         new CoffeeWithPrice("Cappuccino", 2.5)}
     * }</pre>
     * When setting the header type to {@code VERTICAL}</br>
     * Then the report will present the following table
     * <pre>{@code
     *     | name         | Espresso | Cappuccino |
     *     | price in EUR | 2.0      | 2.5        |
     * }</pre>
     * <p>
     * The header type {@code BOTH} <em>cannot</em> be applied to POJO lists
     * </p>
     *
     * @return the header type of the table.
     */
    HeaderType header() default HORIZONTAL;

    /**
     * Whether to transpose the resulting table in the report or not.
     * <h2>Example</h2>
     * Given the following data.
     * <pre>
     * {@code new Object[][] {
     *     { "a1", "a2", "a3" },
     *     { "b1", "b2", "b3" },
     *     { "c1", "c2", "c3" }}
     * }
     * </pre>
     * When transpose is set to {@code true}
     * Then the table in the report will look as follows:
     * <pre>
     *     | a1 | b1 | c1 |
     *     +----+----+----+
     *     | a2 | b2 | c2 |
     *     | a3 | b3 | c3 |
     * </pre>
     * instead of
     * <pre>
     *     | a1 | a2 | a3 |
     *     +----+----+----+
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     */
    boolean transpose() default false;

    /**
     * Specifies which fields should be excluded in the report.
     * <p>
     * If {@link #includeFields()} is set, then this attribute has no effect    
     * 
     * <p>
     * Makes only sense when supplying a list of POJOs or a single POJO.
     */
    String[] excludeFields() default {};

    /**
     * Specifies which fields should be included in the report.
     * 
     * All fields not in this list will be excluded.
     * <p>
     * Makes only sense when supplying a list of POJOs or a single POJO.
     */
    String[] includeFields() default {};

    /**
     * Explicitly specifies column titles of table header.
     * <p>
     * The first row of the data is <b>not</b> taken as the header row if this attribute is set.
     * <p>
     * When a list of POJOs is given as parameter then this overrides the default behavior of taking the field names
     * as table headers.
     * 
     * <h2>Example</h2>
     * Given the following table argument:
     * <pre>
     * {@code new Object[][] {
     *     { "a1", "a2", "a3" },
     *     { "b1", "b2", "b3" },
     *     { "c1", "c2", "c3" }}
     * }
     * </pre>
     * Then the {@link #columnTitles()} attribute is set as follows:
     * <pre>
     * columnTitles = { "t1", "t2", "t3" }    
     * </pre>
     * Then the resulting table will look as follows
     * <pre>
     *     | t1 | t2 | t3 |
     *     +----+----+----+
     *     | a1 | a2 | a3 |
     *     | b1 | b2 | b3 |
     *     | c1 | c2 | c3 |
     * </pre>
     * 
     * @since 0.7.1
     */
    String[] columnTitles() default {};

    /**
     * Whether or not columns with only {@code null} values are shown or not.
     * Default is to not show them.
     */
    boolean includeNullColumns() default false;

    public enum HeaderType {
        /**
         * The table has no header
         */
        NONE,

        /**
         * Treat the first row as a header
         */
        HORIZONTAL,

        /**
         * Treat the first column as a header
         */
        VERTICAL,

        /**
         * Treat both, the first row and the first column as headers
         */
        BOTH;

        public boolean isHorizontal() {
            return this == HORIZONTAL || this == BOTH;
        }

        public boolean isVertical() {
            return this == VERTICAL || this == BOTH;
        }
    }
}
