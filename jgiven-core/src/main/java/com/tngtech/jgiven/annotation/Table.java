package com.tngtech.jgiven.annotation;

import static com.tngtech.jgiven.annotation.Table.HeaderType.HORIZONTAL;

import java.lang.annotation.*;

import com.tngtech.jgiven.format.table.DefaultRowFormatterFactory;
import com.tngtech.jgiven.format.table.DefaultTableFormatter;
import com.tngtech.jgiven.format.table.RowFormatterFactory;
import com.tngtech.jgiven.format.table.TableFormatterFactory;
import com.tngtech.jgiven.impl.util.AnnotationUtil;

/**
 * Marks the parameter of a step method as a data table.
 * Such parameters are represented as tables in the report.
 * <p>
 * In principle, every object can be represented as a table. However, JGiven treats certain types of objects
 * in a special way.
 * <p>
 * If a parameter implements the {@link java.lang.Iterable} or is an instance of an array then each element of
 * the Iterable is interpreted as a single row of the table. Otherwise JGiven will only create a single row.
 * <p>
 * The elements are again interpreted differently whether they are instances of {@link java.lang.Iterable} or not.
 * <p>
 * If the elements are instances of Iterable then each element becomes a cell in the row.
 * Note that the first list is taken as the header of the table if the {@link Table#columnTitles()} is not set.
 * <p>
 * If the elements are not instances of Iterable, the field names become the headers and field values the data.
 * This can be overridden by the {@link #objectFormatting()} attribute.
 * <p>
 * It is also possible to completely replace the way JGiven translates arguments to tables by using the {@link #formatter()} attribute    
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
     * When setting the header type to {@code VERTICAL}<br>
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
     * 
     * @since 0.7.1
     */
    boolean includeNullColumns() default false;

    /**
     * Automatically number the rows of the table
     * Default is not to generate one.
     * <p>
     * If the table has a horizontal header, the generated column has header '#'.
     * To use a different header use {@link #numberedRowsHeader}.
     * 
     * @since 0.8.2
     */
    boolean numberedRows() default false;

    /**
     * Like {@link #numberedRows} but specifies a different header
     * for the generated column. This implicitly sets {@see #numberedRows} to {@code true}. 
     * <p>
     * Note that in case the table has no horizontal header a {@see JGivenWrongUsageException}
     * will be thrown if this value is set.
     * @since 0.8.2
     */
    String numberedRowsHeader() default AnnotationUtil.ABSENT;

    /**
     * Automatically number the columns of a table
     * Default is not to generate one.
     * <p>
     * If the table has a vertical header, the generated row has header '#'.
     * To use a different header use {@link #numberedColumnsHeader}.
     * @since 0.8.2
     */
    boolean numberedColumns() default false;

    /**
     * Like {@link #numberedColumns} but specifies a different header
     * for the generated row. This implicitly sets {@see #numberedColumns} to {@code true}. 
     * <p>
     * Note that in case the table has no vertical header a {@see JGivenWrongUsageException}
     * will be thrown if this value is set.
     * @since 0.8.2
     */
    String numberedColumnsHeader() default AnnotationUtil.ABSENT;

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

    /**
     * The formatter to use to translate the parameter object to a table.
     * If you only want to override how POJOs are formatted you should
     * use the {@link #objectFormatting()} or {@link #rowFormatter()} attribute.
     *
     * @since 0.10.0
     */
    Class<? extends TableFormatterFactory> formatter() default DefaultTableFormatter.Factory.class;

    /**
     * How to format rows when the rows are plain Objects, i.e. no Iterables.
     * Note that this setting is ignored if a different {@link #rowFormatter()} is set.
     * In addition, JGiven will automatically switch to the {@link com.tngtech.jgiven.annotation.Table.ObjectFormatting#PLAIN}
     * formatter when there is an additional formatting annotation besides the {@code @Table} annotation.
     * 
     * @since 0.10.0
     */
    ObjectFormatting objectFormatting() default ObjectFormatting.FIELDS;

    /**
     * Possible choices for the {@link #objectFormatting()} attribute.
     * 
     * @since 0.10.0
     */
    public enum ObjectFormatting {
        /**
         * Each field of the object becomes a column in the table
         */
        FIELDS,

        /**
         * There is only one column and the values are created by formatting
         * each Object using the standard JGiven formatting. 
         */
        PLAIN
    }

    /**
     * Specifies a factory to create a custom {@link com.tngtech.jgiven.format.table.RowFormatter}
     * that is used to format POJOs each row of the table.
     * <p>
     *     The default implementation evaluates the {@link #objectFormatting()} attribute and
     *     creates a corresponding RowFormatter
     * </p>
     *
     * @since 0.10.0
     */
    Class<? extends RowFormatterFactory> rowFormatter() default DefaultRowFormatterFactory.class;

}
