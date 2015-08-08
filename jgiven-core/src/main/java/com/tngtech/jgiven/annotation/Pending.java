package com.tngtech.jgiven.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks methods of step definitions as not implemented yet.
 * Such steps will not be executed, but will appear in
 * the report as pending.
 * <p>
 * This is useful if one already wants to define the scenario without
 * already implementing all steps, for example, to verify that
 * all acceptance criteria of a story are covered by the scenario.
 * <p>
 * Annotating a stage class indicates
 * that no step is implemented yet.
 * <p>
 * Finally, a test method can be annotated to indicate that the whole
 * test is not implemented yet. The test will then be ignored by the testing-framework.
 * (In fact an AssumptionException is thrown. It depends on the test runner how this
 * is interpreted)
 * <i>Currently only works for JUnit</i>
 *
 * <h2>Example</h2>
 * <pre>
 * {@literal @}Pending
 * public void my_cool_new_feature() {
 *
 * }
 * </pre>
 *
 * @since 0.8.0
 */
@Documented
@Inherited
@Retention( RUNTIME )
@Target( { METHOD, TYPE } )
public @interface Pending {
    /**
     * Optional description to describe when the implementation will be done.
     */
    String value() default "";

    /**
     * Instead of only reporting pending steps,
     * the steps are actually executed.
     * This is useful to see whether some steps fail, for example.
     * Failing steps, however, have no influence on the overall test result.
     */
    boolean executeSteps() default false;

    /**
     * If <b>no</b> step fails during the execution of the test,
     * the test will fail.
     * <p>
     * This makes sense if one ensures that a not implemented feature
     * always leads to failing tests in the spirit of test-driven development.
     * <p>
     * If this is true, the <code>executeSteps</code> attribute is implicitly <code>true</code>.
     */
    boolean failIfPass() default false;
}
