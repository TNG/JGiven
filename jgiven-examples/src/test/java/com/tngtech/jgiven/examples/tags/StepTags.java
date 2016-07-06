package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

/**
 * This example demonstrates that tag annotations can also be provided
 * by stage methods and stage classes.
 *
 * Providing tags through steps rather than tests has the benefit that
 * all new tests using these steps will automatically be tagged accordingly.
 * Furthermore, it allows tagging many tests by only tagging an individual
 * step method or class.
 */
public class StepTags extends SimpleScenarioTest<StepTags.Steps> {

    @Test
    public void premium_members_can_order_premium_products() {
        given().a_premium_customer()
            .and().a_product()
            .and().the_product_is_only_available_for_premium_members();
        when().the_customer_orders_the_product();
        then().the_product_is_shipped();
    }

    @Shop
    public static class Steps extends Stage<Steps> {
        @PremiumMembership
        Steps a_premium_customer() {
            return this;
        }

        Steps a_product() {
            return this;
        }

        @PremiumProduct
        Steps the_product_is_only_available_for_premium_members() {
            return this;
        }

        Steps the_customer_orders_the_product() {
            return this;
        }

        Steps the_product_is_shipped() {
            return this;
        }
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface Shop {}

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface Premium {}

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @Premium
    @interface PremiumMembership {}

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @Premium
    @interface PremiumProduct {}

}
