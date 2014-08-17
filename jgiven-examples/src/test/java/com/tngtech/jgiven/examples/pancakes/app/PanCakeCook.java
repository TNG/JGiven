package com.tngtech.jgiven.examples.pancakes.app;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PanCakeCook implements Cook {

    @Override
    public String fryDoughInAPan( Set<String> dough ) {
        if( dough.contains( "egg" ) && dough.contains( "milk" ) && dough.contains( "flour" ) ) {
            return "pancake";
        }
        return "mishmash";
    }

    @Override
    public Set<String> makeADough( List<String> ingredients ) {
        return new HashSet<String>( ingredients );
    }

}
