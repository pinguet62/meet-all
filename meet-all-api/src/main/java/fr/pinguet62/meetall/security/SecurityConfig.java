package fr.pinguet62.meetall.security;

import fr.pinguet62.meetall.config.OpenApiConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static fr.pinguet62.meetall.security.SecretKeyUtils.fromString;

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            @Value("${spring.security.oauth2.resourceserver.jwt.key-value}") String jwtSymmetricKey,
            OpenApiConfig openApiConfig) {
        // @formatter:off
        return http
                .cors()
                        .configurationSource(new ApplicationCorsConfigurationSource())
                .and()
                // JWT config
                .oauth2ResourceServer()
                        .jwt()
                                .jwtDecoder(NimbusReactiveJwtDecoder.withSecretKey(fromString(jwtSymmetricKey)).build())
                                .jwtAuthenticationConverter(JwtToApplicationAuthenticationConverter::convert)
                        .and()
                .and()
                // public/private routes
                .authorizeExchange()
                        .pathMatchers(openApiConfig.getPublicRoutesPathMatchers())
                                .permitAll()
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
