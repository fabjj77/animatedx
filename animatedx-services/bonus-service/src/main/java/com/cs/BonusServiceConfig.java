package com.cs;

import com.cs.config.CoreServiceConfig;
import com.cs.game.GameServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.bonus", "com.cs.promotion"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.bonus", "com.cs.promotion"})
@Import({CoreServiceConfig.class, GameServiceConfig.class, SystemServiceConfig.class})
@PropertySource("classpath:bonus-service.properties")
public class BonusServiceConfig {
}
