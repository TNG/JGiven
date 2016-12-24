package com.tngtech.jgiven.example.springboot;

import org.springframework.http.HttpStatus;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.example.springboot.formatter.HttpStatusFormatter;

public class HelloJGivenConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        setFormatter( HttpStatus.class, new HttpStatusFormatter() );
    }

}
