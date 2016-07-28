package com.cs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Hadi Movaghar
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.cs.payment"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.payment"})
@Import({PlayerServiceConfig.class})
@PropertySource("classpath:payment.properties")
public class PaymentServiceConfig {

    @Configuration
    @Profile("default")
    @PropertySource("classpath:/staging/adyen.staging.properties")
    public static class StagingPaymentConfig {
    }

    @Configuration
    @Profile("production")
    @PropertySource("classpath:production/adyen.production.properties")
    public static class ProductionPaymentConfig {
    }
}
