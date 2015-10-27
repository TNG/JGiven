package com.tngtech.jgiven.integration.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Enables JGiven's spring support. To be used on @{@link Configuration} classes as follows:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableJGiven
 * public class AppConfig {
 * ...
 * }</pre>
 *
 *
 * @since 0.8.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(JGivenSpringConfiguration.class)
@Documented
public @interface EnableJGiven {

}
