package com.tngtech.jgiven.examples.userguide;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

//tag::noPackage[]
public class MyStage {

    protected CustomerBuilder customerBuilder;

    @ProvidedScenarioState
    protected Customer customer;

    public MyStage a_customer() {
        customerBuilder = new CustomerBuilder();
        return this;
    }

    public MyStage the_customer_has_name( String name ) {
        customerBuilder.withName( name );
        return this;
    }

    @AfterStage
    public void buildCustomer() {
        if (customerBuilder != null) {
            customer = customerBuilder.build();
        }
    }
 }
//end::noPackage[]