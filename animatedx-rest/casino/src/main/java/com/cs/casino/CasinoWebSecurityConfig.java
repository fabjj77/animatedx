package com.cs.casino;

import com.cs.casino.security.CasinoCookieContent;
import com.cs.casino.security.LoginAttemptsRecordingAuthenticationProvider;
import com.cs.casino.security.PersistedSessionRegistryImpl;
import com.cs.casino.security.PlayerLogonService;
import com.cs.player.PlayerService;
import com.cs.rest.RestConfig;
import com.cs.session.PlayerSessionRegistryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvcSecurity
public class CasinoWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String LOGIN_URL = "/api/players/login";
    private static final String LOGIN_SUCCESSFUL_URL = "/api/players/login/success";
    private static final String LOGIN_FAILURE_URL = "/api/players/login/failure";
    private static final String LOGOUT_URL = "/api/players/logout";
    private static final String LOGOUT_SUCCESSFUL_URL = "/api/players/logout/success";
    private static final String INVALID_SESSION_URL = "/api/players/session/invalid";
    private static final String SESSION_EXPIRED_URL = "/api/players/session/expired";

    @Autowired
    private PlayerLogonService playerLogonService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PlayerSessionRegistryRepository playerSessionRegistryRepository;

    @Override
    public void configure(final WebSecurity web)
            throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http)
            throws Exception {
        final String[] allowedPostUrls = {"/api/players", "/api/players/password/reset/create", "/api/players/password/reset", "/api/players/validate/email",
                "/api/players/validate/nickname", INVALID_SESSION_URL, SESSION_EXPIRED_URL};
        final String[] allowedGetUrls = {"/api/avatars/signup", "/api/games", "/api/games/touch", "/api/games/leaderboard", "/api/games/leaderboard/**", "/api/levels",
                "/api/players/verify/**", LOGIN_URL, LOGIN_FAILURE_URL, LOGOUT_URL, LOGOUT_SUCCESSFUL_URL, INVALID_SESSION_URL, SESSION_EXPIRED_URL};

        //@formatter:off
        http
            .csrf()
                .disable()
//                .csrfTokenRepository(csrfTokenRepository)
//                .and()
            .headers()
                .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
                .and()
            .authorizeRequests()
                .antMatchers(GET, allowedGetUrls).permitAll()
                .antMatchers(POST, allowedPostUrls).permitAll()
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(LOGIN_URL)
                .defaultSuccessUrl(LOGIN_SUCCESSFUL_URL)
                .failureUrl(LOGIN_FAILURE_URL)
                .permitAll()
                .and()
            .logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessUrl(LOGOUT_SUCCESSFUL_URL)
                .invalidateHttpSession(true)
                .deleteCookies(RestConfig.JSESSIONID, CasinoCookieContent.COOKIE_NAME)
                .permitAll()
                .and()
            .sessionManagement()
                .sessionFixation()
                    .changeSessionId()
                .invalidSessionUrl(INVALID_SESSION_URL)
                .maximumSessions(1)
                    .expiredUrl(SESSION_EXPIRED_URL)
                    .sessionRegistry(sessionRegistry());

        //@formatter:on
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    private LoginAttemptsRecordingAuthenticationProvider authenticationProvider() {
        final LoginAttemptsRecordingAuthenticationProvider authenticationProvider = new LoginAttemptsRecordingAuthenticationProvider(playerService);
        authenticationProvider.setUserDetailsService(playerLogonService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new PersistedSessionRegistryImpl(playerSessionRegistryRepository);
    }
}
