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
     */
    private boolean prependType;

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
        return prependType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
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
        this.prependType = prependType;
        return this;
    }

    @Override
    public String toString() {
        if( value != null ) {
            String valueString = getValueString();
            if( prependType ) {
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

}
