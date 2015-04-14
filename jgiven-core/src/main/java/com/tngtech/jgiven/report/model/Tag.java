package com.tngtech.jgiven.report.model;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * A tag represents a Java annotation of a scenario-test.
 */
public class Tag {
    /**
     * The name/type of this tag
     */
    private final String name;

    /**
     * An optional value
     * Guaranteed to be either of type {@code String} or of type {@code List<String>}
     */
    private Object value;

    /**
     * An optional description.
     */
    private String description;

    /**
     * Whether the type should be prepended in the report.
     * <p>
     * Is either {@code true} or {@code null}    
     */
    private Boolean prependType;

    /**
     * An optional color that is used in reports 
     */
    private String color;

    /**
     * An optional cssClass used in HTML reports
     */
    private String cssClass;

    public Tag( String name ) {
        this.name = name;
    }

    public Tag( String name, Object value ) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean isPrependType() {
        return prependType == null ? false : true;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setColor( String color ) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setCssClass( String cssClass ) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    public String getCssClassOrDefault() {
        return cssClass == null ? "tag-" + getName() : cssClass;
    }

    @SuppressWarnings( "unchecked" )
    public List<String> getValues() {
        if( value == null ) {
            return Collections.emptyList();
        }
        if( value instanceof String ) {
            return Lists.newArrayList( (String) value );
        }
        return (List<String>) value;
    }

    public void setValue( List<String> values ) {
        this.value = values;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public Tag setPrependType( boolean prependType ) {
        this.prependType = prependType ? true : null;
        return this;
    }

    @Override
    public String toString() {
        if( value != null ) {
            String valueString = getValueString();
            if( isPrependType() ) {
                return getName() + "-" + valueString;
            }
            return valueString;
        }
        return getName();
    }

    public String getValueString() {
        if( value == null ) {
            return null;
        }
        return Joiner.on( ", " ).join( getValues() );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( getName(), value );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj ) {
            return true;
        }
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        Tag other = (Tag) obj;
        return Objects.equal( getName(), other.getName() )
                && Objects.equal( value, other.value );
    }

    /**
     * Returns a string representation where all non-alphanumeric characters are replaced with an underline (_).
     * In addition, the result is cut-off at a length of 255 characters.
     * 
     * @return a string representation without special characters
     */
    public String toEscapedString() {
        List<String> parts = Lists.newArrayList( getName() );
        parts.addAll( getValues() );
        String escapedString = escape( Joiner.on( '-' ).join( parts ) );
        return escapedString.substring( 0, Math.min( escapedString.length(), 255 ) );
    }

    static String escape( String string ) {
        return string.replaceAll( "[^\\p{Alnum}-]", "_" );
    }
}
