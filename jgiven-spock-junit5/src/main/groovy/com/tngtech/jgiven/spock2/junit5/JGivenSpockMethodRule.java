package com.tngtech.jgiven.spock2.junit5;

import com.tngtech.jgiven.impl.ScenarioBase;
import com.tngtech.jgiven.junit.JGivenMethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import spock.lang.Specification;

/**
 * JUnit Rule to enable JGiven with Spock Framework
 *
 * @since 0.17.0
 */
public class JGivenSpockMethodRule extends JGivenMethodRule {

    /**
     * @since 0.17.0
     */
    public JGivenSpockMethodRule(ScenarioBase scenario) {
        super(scenario);
    }

    @Override
    protected void starting(Statement base, FrameworkMethod testMethod, Object target) {
        super.starting(base, testMethod, target);
        scenario.getScenarioModel().setDescription(methodNameFromSpec(target));
    }

    private String methodNameFromSpec(Object target) {
        return ((Specification) target).getSpecificationContext().getCurrentIteration().getParent().getName();
    }
}
