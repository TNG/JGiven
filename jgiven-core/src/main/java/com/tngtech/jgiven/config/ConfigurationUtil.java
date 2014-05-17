package com.tngtech.jgiven.config;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.impl.util.ReflectionUtil;

public class ConfigurationUtil {

    public static AbstractJGivenConfiguraton getConfiguration( Class<? extends Object> testClass ) {
        JGivenConfiguration annotation = testClass.getAnnotation( JGivenConfiguration.class );
        if( annotation == null ) {
            return new DefaultConfiguration();
        }

        AbstractJGivenConfiguraton result = ReflectionUtil.newInstance( annotation.value() );
        result.configure();
        return result;
    }

}
