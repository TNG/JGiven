package com.tngtech.jgiven.report.model;

import com.google.common.base.Objects;

public class ArgumentInfo {
    /**
     * In case this word can be replaced by a parameter name,
     * e.g. for data tables, this value is set, otherwise it is {@code null}.
     * The parameter name is in general taken from scenario parameters.
     * In case of a derived parameter the parameter name is actually equal to the
     * argumentName.
     *
     */
    private String parameterName;

    /**
     * The name of the argument as declared in the step method.
     * Should never be {@code null}.
     */
    private String argumentName;

    /**
     * The value of the argument after formatting has been applied.
     * Can be {@code null}
     */
    private String formattedValue;

    /**
     * Is set when the value of the argument is a data table value,
     * otherwise is {@code null}
     */
    private DataTable dataTable;

    public void setParameterName( String parameterName ) {
        this.parameterName = parameterName;
    }

    /**
     * @throws NullPointerException in case their is no parameter name
     * @return the parameter name if there is one
     */
    public String getParameterName() {
        if( parameterName == null ) {
            throw new NullPointerException( "Argument has no parameter name" );
        }
        return parameterName;
    }

    /**
     * @return whether this is argument is a parameter or not
     */
    public boolean isParameter() {
        return parameterName != null;
    }

    public void setArgumentName( String argumentName ) {
        this.argumentName = argumentName;
    }

    public String getArgumentName() {
        return argumentName;
    }

    public void setFormattedValue( String formattedValue ) {
        this.formattedValue = formattedValue;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    public void setDataTable( DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public boolean isDataTable() {
        return dataTable != null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( parameterName, argumentName, dataTable );
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
        ArgumentInfo other = (ArgumentInfo) obj;
        return Objects.equal( parameterName, other.parameterName )
                && Objects.equal( argumentName, other.argumentName )
                && Objects.equal( dataTable, other.dataTable );
    }

}
