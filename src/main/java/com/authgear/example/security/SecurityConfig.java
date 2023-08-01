package com.authgear.example.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Value("${authgear.oauth2.end-session-endpoint}")
    private String endSessionEndpoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                // allow anonymous access to the root page
                .requestMatchers("/").permitAll()
                // authenticate all other requests
                .anyRequest().authenticated())
            // enable OAuth2/OIDC
            .oauth2Login(withDefaults())
            // configure logout handler
            .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .addLogoutHandler(oidcLogoutHandler()));
        return http.build();
    }

    LogoutHandler oidcLogoutHandler() {
        return (request, response, authentication) -> {
            try {
                response.sendRedirect(endSessionEndpoint);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
