package com.tngtech.jgiven.report.model;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

/**
 * A tag represents a Java annotation of a scenario-test
 */
public class Tag {
    private final String name;

    /**
     * An optional value 
     * Guaranteed to be either of type String or of type String[].
     */
    private Object value;

    /** 
     * An optional description
     */
    private String description;

    /**
     * Whether the type should be prepended in the report
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

    public Object getValue() {
        return value;
    }

    public void setValue( Object value ) {
        if( value != null && !( value instanceof String ) ) {
            if( !( value.getClass().isArray() && value.getClass().getComponentType().isAssignableFrom( String.class ) ) ) {
                throw new IllegalArgumentException( "value is neither null, String nor String[]" );
            }
        }
        this.value = value;
    }

    public Tag setPrependType( boolean prependType ) {
        this.prependType = prependType;
        return this;
    }

    @Override
    public String toString() {
        if( getValue() != null ) {
            String valueString = getValueString();
            if( prependType ) {
                return getName() + "-" + valueString;
            }
            return valueString;
        }
        return getName();
    }

    public String getValueString() {
        if( getValue() == null ) {
            return null;
        }
        if( getValue().getClass().isArray() ) {
            return Joiner.on( ", " ).join( (String[]) getValue() );
        }
        return getValue().toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( getName(), getValue() );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        Tag other = (Tag) obj;
        return Objects.equal( getName(), other.getName() )
                && Objects.equal( getValue(), other.getValue() );
    }

}
