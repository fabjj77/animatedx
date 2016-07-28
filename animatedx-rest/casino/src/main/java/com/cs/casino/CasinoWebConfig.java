package com.cs.casino;

import com.cs.PaymentServiceConfig;
import com.cs.PlayerServiceConfig;
import com.cs.SystemServiceConfig;
import com.cs.game.GameServiceConfig;
import com.cs.rest.RestConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.cs.casino"})
@Import({PlayerServiceConfig.class, PaymentServiceConfig.class, GameServiceConfig.class, RestConfig.class, CasinoWebSocketConfig.class, SystemServiceConfig.class})
public class CasinoWebConfig extends WebMvcConfigurationSupport {

    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        final RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
        handlerMapping.setRemoveSemicolonContent(false);
        return handlerMapping;
    }
}
