package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class ArgumentWord extends Word {
    public ArgumentWord() {}

    public ArgumentWord( String value ) {
        super( value );
    }

    /**
     * If this word is an argument, whether it is a case
     * argument or not.
     */
    private boolean isCaseArg;

    /**
     * In case this word is a case argument (isCaseArg == true)
     * this field is set to the corresponding parameter index
     */
    private int parameterIndex;

    @Override
    public boolean isArg() {
        return true;
    }

    public void setParameterIndex( int i ) {
        isCaseArg = true;
        parameterIndex = i;
    }

    public boolean isCaseArg() {
        return isCaseArg;
    }

    public int getParameterIndex() {
        return parameterIndex;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 31 +
                Objects.hashCode( isCaseArg, parameterIndex );
    }

    @Override
    public boolean equals( Object obj ) {
        if( this == obj )
            return true;
        if( obj == null )
            return false;
        if( getClass() != obj.getClass() )
            return false;
        ArgumentWord other = (ArgumentWord) obj;
        if( !super.equals( other ) ) {
            return false;
        }
        return Objects.equal( isCaseArg, other.isCaseArg ) &&
                Objects.equal( parameterIndex, other.parameterIndex );
    }
}
