package com.cs;

import com.cs.config.CoreServiceConfig;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

/**
 * @author Joakim Gottzén
 */
@Configuration
@ComponentScan(basePackages = {"com.cs.audit", "com.cs.messaging.sftp", "com.cs.control", "com.cs.report", "com.cs.session", "com.cs.whitelist"},
               excludeFilters = @Filter(Repository.class))
@EnableJpaRepositories({"com.cs.audit", "com.cs.control", "com.cs.session", "com.cs.whitelist"})
@Import({CoreServiceConfig.class})
@ImportResource("classpath:spring/integration/sftp-Context.xml")
@PropertySource("classpath:netrefer.properties")
public class SystemServiceConfig {
}
