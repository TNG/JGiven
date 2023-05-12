package com.tngtech.jgiven.annotation;

import com.tngtech.jgiven.format.POJOAnnotationFormatter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A special format annotation to format POJOs
 * @since 0.15.0
 */
@Documented
@AnnotationFormat( value = POJOAnnotationFormatter.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface POJOFormat {

    /**
     * Enumeration of opening/closing brackets pair :
     * <ul>
     * <li>{@link #NONE} : no brackets</li>
     * <li>{@link #PARENTHESES} : <code>(...)</code></li>
     * <li>{@link #SQUARE} : <code>[...]</code></li>
     * <li>{@link #BRACES} : <code>{...}</code></li>
     * <li>{@link #POINTY} : <code>&lt;...&gt;</code></li>
     * <li>{@link #CHEVRONS} : <code>&lt;&lt;...&gt;&gt;</code></li>
     * <li>{@link #DOUBLE_QUOTE} : <code>"..."</code></li>
     * <li>{@link #SINGLE_QUOTE} : <code>'...'</code></li>
     * </ul>
     */
    enum BracketsEnum {
        NONE( "", "" ),
        PARENTHESES( "(", ")" ),
        SQUARE( "[", "]" ),
        BRACES( "{", "}" ),
        POINTY( "<", ">" ),
        CHEVRONS( "<<", ">>" ),
        DOUBLE_QUOTE( "\"", "\"" ),
        SINGLE_QUOTE( "'", "'" ),
        ;

        private String opening;
        private String closing;

        BracketsEnum( String opening, String closing ) {
            this.opening = opening;
            this.closing = closing;
        }

        public String getOpening() {
            return opening;
        }

        public String getClosing() {
            return closing;
        }

    }

    /**
     * Specifies which fields should be excluded in the report.
     * <p>
     * If {@link #includeFields()} is set, then this attribute has no effect
     *
     * <p>
     */
    String[] excludeFields() default {};

    /**
     * Specifies which fields should be included in the report.
     *
     * All fields not in this list will be excluded.
     * <p>
     */
    String[] includeFields() default {};

    /**
     * Whether or not columns with only {@code null} values are shown or not.
     * Default is to not show them.
     *
     */
    boolean includeNullColumns() default false;

    /**
     * When set to <code>true</code>, each formatted field value is prefixed by its field name
     */
    boolean prefixWithFieldName() default false;

    /**
     * Specify a field separator
     */
    String fieldSeparator() default ",";

    /**
     * Specify the opening/closing brackets pair to set POJO string representation apart of its parent (step) string representation.
     *
     * <p>
     * Default brackets pair is {@link BracketsEnum#SQUARE}.<br>
     * When no brackets is needed, consider specify {@link BracketsEnum#NONE}
     * </p>
     *
     * @See {@link BracketsEnum}
     */
    BracketsEnum brackets() default BracketsEnum.SQUARE;

    /**
     * Specify a custom {@link NamedFormats} annotation
     *
     * <p>
     * The {@link NamedFormat} defined in this set will be used when formatting
     * POJOs fields.<br>
     * </p>
     *
     */
    Class<? extends Annotation> fieldFormatsAnnotation() default Annotation.class;

    /**
     * Specify an array of {@link NamedFormat} to use when formatting POJOs
     * fields.
     * <p>
     * When a {@link NamedFormat#name()} matches a field name, field value is
     * formatted using this {@link NamedFormat}.
     * </p>
     *
     * <p>
     * Note: when set, has precedence over {@link #fieldFormatsAnnotation()}
     * </p>
     *
     * @See {@link #fieldFormatsAnnotation()}
     */
    NamedFormat[] fieldFormats() default {};

}
