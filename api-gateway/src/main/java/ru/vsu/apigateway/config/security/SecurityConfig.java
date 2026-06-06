package ru.vsu.apigateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final ApiAuthorizationHeaderFilter apiAuthorizationHeaderFilter;

    public SecurityConfig(ApiAuthorizationHeaderFilter apiAuthorizationHeaderFilter) {
        this.apiAuthorizationHeaderFilter = apiAuthorizationHeaderFilter;
    }

    @Bean
    SecurityFilterChain apiGatewaySecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .requestCache(requestCache -> requestCache.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests -> requests
                .anyRequest().permitAll()
            )
            .addFilterBefore(apiAuthorizationHeaderFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
