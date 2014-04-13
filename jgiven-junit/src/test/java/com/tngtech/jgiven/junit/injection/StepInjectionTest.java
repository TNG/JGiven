package com.tngtech.jgiven.junit.injection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.ScenarioTest;

public class StepInjectionTest extends ScenarioTest {
    @ScenarioStage
    CustomerSteps customerSteps;

    @ScenarioStage
    ThenCustomer thenCustomer;

    @Test
    public void steps_are_injected_into_test_case() {
        customerSteps.given().a_customer()
            .with().age( 34 );
        thenCustomer.then().customer_has_age( 34 );
    }

    static class CustomerSteps extends Stage<CustomerSteps> {
        @ProvidedScenarioState
        Customer customer;

        public CustomerSteps a_customer() {
            customer = new Customer();
            customer.name = "TestName";
            customer.age = 42;
            return self();
        }

        public CustomerSteps age( int age ) {
            customer.age = age;
            return self();
        }
    }

    static class ThenCustomer extends Stage<ThenCustomer> {
        @ExpectedScenarioState
        Customer customer;

        public void customer_has_age( int expectedAge ) {
            assertThat( customer.age ).isEqualTo( expectedAge );
        }
    }

    static class Customer {
        String name;
        int age;
    }
}
