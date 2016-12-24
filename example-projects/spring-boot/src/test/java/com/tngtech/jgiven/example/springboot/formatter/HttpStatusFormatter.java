package com.tngtech.jgiven.example.springboot.formatter;

import com.tngtech.jgiven.format.Formatter;
import org.springframework.http.HttpStatus;

import java.lang.annotation.Annotation;

public class HttpStatusFormatter implements Formatter<HttpStatus> {

    @Override
    public String format( HttpStatus httpStatus, Annotation... annotations ) {
        return httpStatus.getReasonPhrase() + " (" + httpStatus.value() + ")";
    }
}
