package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

/**
 * A tag represents a Java annotation of a scenario-test
 */
public class Tag {
    public String name;

    /**
     * Guaranteed to be either of type String or of type String[].
     * Can be null.
     */
    public Object value;

    @Override
    public String toString() {
        return name + ( ( value != null ) ? "-" + value : "" );
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( name, value );
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
        return Objects.equal( name, other.name )
                && Objects.equal( value, other.value );
    }

}
