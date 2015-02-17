package com.tngtech.jgiven.junit;

public class BridgeMethodTest extends AbstractBridgeMethodTest<BridgeMethodTest.SubclassBridgeMethodTestStage> {

    static class SubclassBridgeMethodTestStage extends AbstractBridgeMethodTest.BridgeMethodTestStage {
        @Override
        SubclassBridgeMethodTestStage method_that_is_overidden_with_different_return_type() {
            return this;
        }
    }
}
