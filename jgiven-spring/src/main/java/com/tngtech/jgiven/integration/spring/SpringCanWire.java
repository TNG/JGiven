package com.tngtech.jgiven.integration.spring;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.tngtech.jgiven.integration.CanWire;
/**
 * @deprecated use SpringScenarioExecutor instead
 */
@Deprecated
public class SpringCanWire implements CanWire {

    private final AutowireCapableBeanFactory factory;

    public SpringCanWire( AutowireCapableBeanFactory factory ) {
        this.factory = factory;
    }

    @Override
    public void wire( Object bean ) {
        factory.autowireBean( bean );
    }

}
