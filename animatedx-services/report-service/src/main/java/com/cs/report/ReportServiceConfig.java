package com.cs.report;

import com.cs.PaymentServiceConfig;
import com.cs.PlayerServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Hadi Movaghar
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.report"})
@Import({PlayerServiceConfig.class, PaymentServiceConfig.class})
public class ReportServiceConfig {
}
