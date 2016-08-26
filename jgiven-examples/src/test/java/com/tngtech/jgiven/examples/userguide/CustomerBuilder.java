package com.tngtech.jgiven.examples.userguide;

public class CustomerBuilder  {


    private String name;
    
    public void withName(String name) {
        this.name = name;        
    }

    public Customer build() {
        // TODO Auto-generated method stub
        return new Customer(name);
    }

}
