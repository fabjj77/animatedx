package com.cs.messaging;

import com.cs.config.CoreServiceConfig;
import com.cs.messaging.CommunicationServiceConfig.BrontoIntegrationConfig;
import com.cs.messaging.CommunicationServiceConfig.WebSocketIntegrationConfig;
import com.cs.messaging.email.EmailServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.messaging.email"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.messaging.email"})
@Import({CoreServiceConfig.class, BrontoIntegrationConfig.class, WebSocketIntegrationConfig.class, EmailServiceConfig.class})
@PropertySource("classpath:communication.properties")
public class CommunicationServiceConfig {

    @Configuration
    @ImportResource("classpath:spring/integration/core-integration-Config.xml")
    public static class CoreIntegrationConfig {
    }

    @Configuration
    @EnableJpaRepositories({"com.cs.messaging.bronto"})
    @Import({CoreIntegrationConfig.class})
    @ImportResource("classpath:spring/integration/bronto-integration-Config.xml")
    public static class BrontoIntegrationConfig {
    }

    @Configuration
    @Import({CoreIntegrationConfig.class})
    @ImportResource("classpath:spring/integration/web-socket-integration-Config.xml")
    public static class WebSocketIntegrationConfig {
    }
}
