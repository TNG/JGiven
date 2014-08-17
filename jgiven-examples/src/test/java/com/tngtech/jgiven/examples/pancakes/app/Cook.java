package com.tngtech.jgiven.examples.pancakes.app;

import java.util.List;
import java.util.Set;

public interface Cook {

    String fryDoughInAPan( Set<String> dough );

    Set<String> makeADough( List<String> ingredients );

}
