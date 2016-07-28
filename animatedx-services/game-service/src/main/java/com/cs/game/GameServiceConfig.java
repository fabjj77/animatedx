package com.cs.game;

import com.cs.config.CoreServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Hadi Movaghar
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.game"}, excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.game"})
@Import(CoreServiceConfig.class)
public class GameServiceConfig {

    @Configuration
    @Profile("default")
    @PropertySource("classpath:staging/game.staging.properties")
    public static class StagingCasinoModuleConfig {
    }

    @Configuration
    @Profile("production")
    @PropertySource("classpath:production/game.production.properties")
    public static class ProductionCasinoModuleConfig {
    }
}
