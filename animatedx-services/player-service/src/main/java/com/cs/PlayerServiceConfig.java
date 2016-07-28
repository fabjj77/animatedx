package com.cs;

import com.cs.config.CoreServiceConfig;
import com.cs.game.GameServiceConfig;
import com.cs.messaging.CommunicationServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Joakim Gottzén
 */
@Configuration
@ComponentScan(basePackages = {
        "com.cs.affiliate", "com.cs.avatar", "com.cs.level", "com.cs.player", "com.cs.item", "com.cs.comment", "com.cs.item", "com.cs.agreement"},
               excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.affiliate", "com.cs.avatar", "com.cs.level", "com.cs.player", "com.cs.item", "com.cs.comment", "com.cs.item", "com.cs.agreement"})
@Import({CoreServiceConfig.class, GameServiceConfig.class, SystemServiceConfig.class, CommunicationServiceConfig.class, BonusServiceConfig.class})
@PropertySource("classpath:player-service.properties")
public class PlayerServiceConfig {
}
