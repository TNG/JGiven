package com.tngtech.jgiven.junit.tags;

import com.tngtech.jgiven.junit.ScenarioTest;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractScenarioForTestingTagInheritance<G, W, T> extends ScenarioTest<G, W, T> {

    @Test
    public void ensure_all_tags_are_found() throws Throwable {
        getScenario().finished();

        assertThat(getScenario().getModel().getLastScenarioModel().getTagIds())
                .containsExactly(TestTag.class.getName());

    }
}
