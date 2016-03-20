package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.Quoted;

public class BridgeMethodTest extends AbstractBridgeMethodTest<BridgeMethodTest.SubclassBridgeMethodTestStage> {

    static class SubclassBridgeMethodTestStage<SELF extends SubclassBridgeMethodTestStage<?>> extends
            AbstractBridgeMethodTest.BridgeMethodTestStage<SELF> {
        @Override
        SubclassBridgeMethodTestStage method_that_is_overidden_with_different_return_type() {
            return this;
        }

        @Override
        SELF method_with_formatter_$( @Quoted String text ) {
            return self();
        }

    }
}
