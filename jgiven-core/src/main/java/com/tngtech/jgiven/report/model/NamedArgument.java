package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class NamedArgument {
    public final String name;
    public final Object value;

    public NamedArgument( String name, Object value ) {
        this.name = name;
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( name, value );
    }

    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) {
            return false;
        }
        if( this == obj ) {
            return true;
        }
        if( !this.getClass().equals( obj.getClass() ) ) {
            return false;
        }
        NamedArgument other = (NamedArgument) obj;
        return Objects.equal( this.name, other.name ) && Objects.equal( this.value, other.value );
    }

    @Override
    public String toString() {
        return "NamedArgument [name=" + name + ", value=" + value + "]";
    }

    public Object getValue() {
        return value;
    }
}
