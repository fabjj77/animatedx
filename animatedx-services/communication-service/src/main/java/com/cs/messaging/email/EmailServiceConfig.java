package com.cs.messaging.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.messaging.email"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.messaging.email"})
@PropertySource("classpath:communication.properties")
public class EmailServiceConfig {

    @Value("${email.email-host}")
    private String emailHost;
    @Value("${email.email-port}")
    private Integer emailPort;
    @Value("${email.email-username}")
    private String emailUserName;
    @Value("${email.email-password}")
    private String emailPassword;

    @Bean
    public JavaMailSender javaMailService() {
        final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(emailHost);
        javaMailSender.setUsername(emailUserName);
        javaMailSender.setPassword(emailPassword);
        return javaMailSender;
    }
}
