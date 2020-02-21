package fr.pinguet62.meetall.security;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

class ApplicationCorsConfigurationSource extends UrlBasedCorsConfigurationSource {

    public ApplicationCorsConfigurationSource() {
        registerCorsConfiguration("/**", getCorsConfig());
    }

    private CorsConfiguration getCorsConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();
        config.addAllowedMethod("*");
        return config;
    }

}
