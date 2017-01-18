package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;

/**
 * Allow to specify specific formats ({@link TableFieldFormat}) for part or all
 * of fields of a {@link Table} annotated parameter of a step method.<br>
 * <p>
 * <h3>Example</h3>
 * <h4>Some POJO</h4>
 * 
 * <pre>
 * {
 * 	&#64;code
 * 	class CoffeeWithPrice {
 * 		String name;
 * 		double price_in_EUR;
 * 		Date lastPriceDate;
 * 
 * 		CoffeeWithPrice(String name, double priceInEur, Date lastPriceDate) {
 * 			this.name = name;
 * 			this.price_in_EUR = priceInEur;
 * 			this.lastPriceDate = lastPriceDate;
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * <h4>The Step Method</h4>
 * 
 * <pre>
 * {@code
 *     public SELF the_prices_of_the_coffees_are( @Table @TableFieldsFormats({ @TableFieldFormat(value = "lastPriceDate", format = @Format(value = DateFormatter.class, args = "dd/MM/yyyy")) }) CoffeeWithPrice... prices ) {
 *         ...
 *     }
 * }
 * </pre>
 * 
 * Here we have explicitly set a date formatter (format <code>dd/MM/yyyy</code>)
 * for field <code>lastPriceDate</code> in order to get an easy readable date
 * (in replacement of the default {@link Date#toString()}</code>
 * representation).
 * 
 * <h4>Invocation of the step method</h4>
 * 
 * <pre>
 * {@code
 *     given().the_prices_of_the_coffees_are(
 *         new CoffeeWithPrice("Espresso", 2.0, new Date()),
 *         new CoffeeWithPrice("Cappuccino", 2.5, new Date()));
 * }
 * </pre>
 * 
 * <h4>Text Report</h4>
 * 
 * <pre>
 * {@code
 *     Given the prices of the coffees are
 *
 *          | name       | price in EUR | lastPriceDate |
 *          +------------+--------------+---------------+
 *          | Espresso   | 2.0          | 18/01/2017    |
 *          | Cappuccino | 2.5          | 18/01/2017    |
 *
 *
 * }
 * </pre>
 * 
 * @author dgrandemange
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface TableFieldsFormats {
	/**
	 * @return array of {@link TableFieldFormat}
	 */
	TableFieldFormat[] value();
}
