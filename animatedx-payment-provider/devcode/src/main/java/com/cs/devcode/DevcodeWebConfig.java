package com.cs.devcode;

import com.cs.PaymentServiceConfig;
import com.cs.PlayerServiceConfig;
import com.cs.SystemServiceConfig;
import com.cs.game.GameServiceConfig;
import com.cs.report.ReportServiceConfig;
import com.cs.rest.RestConfig;
import com.cs.user.UserServiceConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author Hadi Movaghar
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.cs.devcode"})
@Import(value = {
        SystemServiceConfig.class, PlayerServiceConfig.class, UserServiceConfig.class, PaymentServiceConfig.class, RestConfig.class, GameServiceConfig.class,
        ReportServiceConfig.class
})
public class DevcodeWebConfig extends WebMvcConfigurationSupport {

    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        final RequestMappingHandlerMapping handlerMapping = super.requestMappingHandlerMapping();
        handlerMapping.setRemoveSemicolonContent(false);
        return handlerMapping;
    }
}
