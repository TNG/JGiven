package com.tngtech.jgiven.impl;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyClassPerformanceTest {

    @Test
    public void test_creation_of_proxy_classes(){
        Set<Long> results = new HashSet<>();
        for( int i = 0; i < 1000; i++ ) {
            ScenarioBase scenario = new ScenarioBase();
            TestStage testStage = scenario.addStage( TestStage.class );
            testStage.something();
            if( i % 100 == 0 ) {
                System.gc();
                long usedMemory = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) / ( 1024 * 1024 );
                System.out.println( "Used memory: " + usedMemory );
                results.add( usedMemory );
            }
        }

        assertThat( results )
                .describedAs( "Only should contains 1 or 2 items, as first iteration might use more memory might contain 2 items" )
                .hasSizeBetween( 1, 2 );
    }

    public static class TestStage {
        public void something(){
        }
    }
}
