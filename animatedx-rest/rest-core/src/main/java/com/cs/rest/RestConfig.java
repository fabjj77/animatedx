package com.cs.rest;

import com.cs.util.DateFormatPatterns;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.google.common.base.Charsets;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.cs.rest", "com.cs.security"})
public class RestConfig extends WebMvcConfigurerAdapter {

    @SuppressWarnings("SpellCheckingInspection")
    public static final String X_XSRF_TOKEN = "X-XSRF-TOKEN";
    public static final String JSESSIONID = "JSESSIONID";

    @Bean
    public JaxbAnnotationIntrospector jaxbAnnotationIntrospector() {
        return new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
    }

    @Bean
    public JacksonAnnotationIntrospector jacksonAnnotationIntrospector() {
        return new JacksonAnnotationIntrospector();
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(jaxbAnnotationIntrospector(), jacksonAnnotationIntrospector()));
        mapper.setDateFormat(new SimpleDateFormat(DateFormatPatterns.ISO_FORMAT_STRING));
        return mapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.getObjectMapper().setAnnotationIntrospector(new AnnotationIntrospectorPair(jaxbAnnotationIntrospector(), jacksonAnnotationIntrospector()));
        converter.getObjectMapper().setDateFormat(new SimpleDateFormat(DateFormatPatterns.ISO_FORMAT_STRING));
        return converter;
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        final StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charsets.UTF_8);
        converters.add(stringHttpMessageConverter);
        converters.add(jsonConverter());
        final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        converters.add(formHttpMessageConverter);
    }

    @Bean
    public SpringValidatorAdapter validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        final HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();
        csrfTokenRepository.setHeaderName(X_XSRF_TOKEN);
        return csrfTokenRepository;
    }
}
