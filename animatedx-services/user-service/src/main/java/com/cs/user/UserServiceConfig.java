package com.cs.user;

import com.cs.persistence.JpaConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Omid Alaepour
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.user"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.user"})
@Import({JpaConfig.class})
@PropertySource("classpath:user-service.properties")
public class UserServiceConfig {
}
