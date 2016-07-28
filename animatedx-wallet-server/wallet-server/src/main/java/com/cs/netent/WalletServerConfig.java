package com.cs.netent;

import com.cs.PaymentServiceConfig;
import com.cs.PlayerServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.cs.netent"})
@Import({PaymentServiceConfig.class, PlayerServiceConfig.class})
public class WalletServerConfig {

    @Configuration
    @Profile("default")
    @PropertySource("classpath:staging/wallet-server.staging.properties")
    public static class StagingWalletServerPropertiesConfig {
    }

    @Configuration
    @Profile("production")
    @PropertySource("classpath:production/wallet-server.production.properties")
    public static class ProductionWalletServerPropertiesConfig {
    }
}
