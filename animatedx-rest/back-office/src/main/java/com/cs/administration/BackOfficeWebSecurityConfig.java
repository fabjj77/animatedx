package com.cs.administration;

import com.cs.administration.security.LoginAttemptsRecordingAuthenticationProvider;
import com.cs.administration.security.UserLogonService;
import com.cs.rest.RestConfig;
import com.cs.user.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import static org.springframework.http.HttpMethod.GET;

/**
 * @author Joakim Gottzén
 */
@Configuration
@EnableWebMvcSecurity
public class BackOfficeWebSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_URL = "/api/users/login";
    private static final String LOGIN_SUCCESSFUL_URL = "/api/users/login/success";
    private static final String LOGIN_FAILURE_URL = "/api/users/login/failure";
    private static final String LOGOUT_URL = "/api/users/logout";
    private static final String LOGOUT_SUCCESSFUL_URL = "/api/users/logout/success";
    private static final String INVALID_SESSION_URL = "/api/users/session/invalid";
    private static final String SESSION_EXPIRED_URL = "/api/users/session/expired";

    @Autowired
    private UserLogonService userLogonService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private CsrfTokenRepository csrfTokenRepository;

    @Override
    public void configure(final WebSecurity web)
            throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http)
            throws Exception {
        final String[] allowedGetUrls = {LOGIN_URL, LOGIN_FAILURE_URL, LOGOUT_URL, LOGOUT_SUCCESSFUL_URL, INVALID_SESSION_URL, SESSION_EXPIRED_URL};

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
                .deleteCookies(RestConfig.JSESSIONID)
                .permitAll()
                .and()
            .sessionManagement()
                .sessionFixation()
                    .changeSessionId()
                .invalidSessionUrl(INVALID_SESSION_URL)
                .maximumSessions(1)
                    .expiredUrl(SESSION_EXPIRED_URL);
        //@formatter:on
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth)
            throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    private LoginAttemptsRecordingAuthenticationProvider authenticationProvider() {
        final LoginAttemptsRecordingAuthenticationProvider authenticationProvider = new LoginAttemptsRecordingAuthenticationProvider(userService);
        authenticationProvider.setUserDetailsService(userLogonService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
