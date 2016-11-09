package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.relocated.guava.annotations.VisibleForTesting;
import com.tngtech.jgiven.relocated.guava.collect.Lists;
import com.tngtech.jgiven.relocated.guava.primitives.Primitives;
import com.tngtech.jgiven.impl.ScenarioBase;

/**
 * Only exists for backwards-compatibility reasons.
 *
 * @deprecated use {@link JGivenMethodRule} instead
 */
@Deprecated
public class ScenarioExecutionRule extends JGivenMethodRule {

    /**
     * @deprecated use {@link JGivenMethodRule}
     */
    @Deprecated
    public ScenarioExecutionRule() {
        super();
    }

    /**
     * @deprecated use {@link JGivenMethodRule}
     */
    @Deprecated
    public ScenarioExecutionRule(JGivenClassRule reportRule, Object testInstance, ScenarioBase scenario ) {
        this( testInstance, scenario );
    }

    /**
     * @deprecated use {@link JGivenMethodRule}
     */
    @Deprecated
    public ScenarioExecutionRule(Object testInstance, ScenarioBase scenario ) {
        this(scenario);
    }

    /**
     * @deprecated use {@link JGivenMethodRule}
     */
    @Deprecated
    public ScenarioExecutionRule(ScenarioBase scenario ) {
        super(scenario);
    }



}
