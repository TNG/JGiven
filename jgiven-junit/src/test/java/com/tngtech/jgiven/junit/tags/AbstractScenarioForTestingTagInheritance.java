package com.tngtech.jgiven.junit.tags;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;

public abstract class AbstractScenarioForTestingTagInheritance<G, W, T> extends ScenarioTest<G, W, T> {

    @Test
    public void ensure_all_tags_are_found() throws Throwable {
        getScenario().finished();

        List<String> tagIds = getScenario().getModel().getLastScenarioModel().getTagIds();
        assertThat( tagIds ).containsAll( Arrays.asList( "TestTag" ) );

    }
}
