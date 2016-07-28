package com.cs.adyen.notifications;

import com.cs.PaymentServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Hadi Movaghar
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.cs.adyen.notifications"})
@Import(PaymentServiceConfig.class)
public class NotificationPortTypeConfig {
}
