package com.tngtech.jgiven.examples.datatable.model;

import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.Quoted;

public class Customer {
    String name;

    @Formatf( value = "(quoted at POJO field level) %s" )
    @Quoted
    String email;

    Address shippingAddress;

    public Customer( String name, String email ) {
        super();
        this.name = name;
        this.email = email;
    }

    public Customer( String name, String email, Address shippingAddress ) {
        super();
        this.name = name;
        this.email = email;
        this.shippingAddress = shippingAddress;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

}
