package com.tngtech.jgiven.examples.userguide;

public class CustomerBuilder  {


    private String name;
    
    public void withName(String name) {
        this.name = name;        
    }

    public Customer build() {
        return new Customer(name);
    }

}
