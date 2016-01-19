package com.tngtech.jgiven.example.springboot;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class HelloStage extends Stage<HelloStage> {

    MockMvc mvc;

    @Autowired
    HelloController helloController;

    private ResultActions mvcResult;

    @BeforeStage
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup( helloController ).build();
    }

    public HelloStage get( @Quoted String path ) throws Exception {
        mvcResult = mvc.perform( MockMvcRequestBuilders.get( path ).accept( MediaType.APPLICATION_JSON ) );
        return this;
    }

    public HelloStage the_status_is( HttpStatus status ) throws Exception {
        mvcResult.andExpect( status().is( status.value() ) );
        return this;
    }

    public HelloStage the_content_is( @Quoted String content ) throws Exception {
        mvcResult.andExpect( content().string( equalTo( content ) ) );
        return this;
    }
}
