package com.cs.casino.player;

import com.cs.audit.AuditService;
import com.cs.avatar.AvatarService;
import com.cs.game.GameService;
import com.cs.item.ItemService;
import com.cs.messaging.email.EmailService;
import com.cs.payment.PaymentService;
import com.cs.payment.transaction.PaymentTransactionFacade;
import com.cs.player.PlayerService;
import com.cs.rest.RestConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import org.mockito.Mock;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvc
@Import(RestConfig.class)
public class TestConfig extends WebMvcConfigurerAdapter {

    @Mock
    private AuditService auditService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private EmailService emailService;
    @Mock
    private GameService gameService;
    @Mock
    private ItemService itemService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private PaymentTransactionFacade paymentTransactionFacade;
    @Mock
    private PlayerService playerService;
    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private FilterChainProxy springSecurityFilter;

    public TestConfig() {
        initMocks(this);
    }

    @Bean
    public AuditService auditService() {
        return auditService;
    }

    @Bean
    public AvatarService avatarService() {
        return avatarService;
    }

    @Bean
    public EmailService emailService() {
        return emailService;
    }

    @Bean
    public GameService gameService() {
        return gameService;
    }

    @Bean
    public ItemService itemService() {
        return itemService;
    }

    @Bean
    public PaymentService paymentService() {
        return paymentService;
    }

    @Bean
    public PaymentTransactionFacade transactionService() {
        return paymentTransactionFacade;
    }

    @Bean
    public PlayerService playerService() {
        return playerService;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return sessionRegistry;
    }

    @Bean
    public FilterChainProxy springSecurityFilter() {
        return springSecurityFilter;
    }
}
