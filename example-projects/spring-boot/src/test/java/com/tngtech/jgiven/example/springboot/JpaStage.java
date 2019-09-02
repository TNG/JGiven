package com.tngtech.jgiven.example.springboot;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@JGivenStage
public class JpaStage {

    @Autowired
    TestEntityManager testEntityManager;


    public JpaStage test_entity_manager_is_defined() {
        assertThat( testEntityManager ).isNotNull();
        return this;
    }

}
