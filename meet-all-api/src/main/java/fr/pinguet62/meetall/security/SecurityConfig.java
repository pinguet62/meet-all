package fr.pinguet62.meetall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtTokenGenerator jwtTokenGenerator) {
        // @formatter:off
        return http
                .cors()
                        .configurationSource(new ApplicationCorsConfigurationSource())
                .and()
                // JWT config
                .oauth2ResourceServer()
                        .jwt()
                                .authenticationManager(new ApplicationAuthenticationManager(jwtTokenGenerator::verifyAndConvertToken))
                        .and()
                .and()
                // public/private routes
                .authorizeExchange()
                        .pathMatchers("/login", "/user", "/photo/*")
                                .permitAll()
                        .anyExchange()
                                .authenticated()
                .and()
                // default: disable // TODO test
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .build();
        // @formatter:on
    }

}
