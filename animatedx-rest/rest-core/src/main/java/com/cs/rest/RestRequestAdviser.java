package com.cs.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author Omid Alaepour.
 */
@ControllerAdvice
public class RestRequestAdviser {
    private final SpringValidatorAdapter validator;

    @Autowired
    public RestRequestAdviser(final SpringValidatorAdapter validator) {
        this.validator = validator;
    }

    @InitBinder
    protected void initBinder(final WebDataBinder binder) {
        binder.initBeanPropertyAccess();
        binder.setValidator(validator);
    }
}
