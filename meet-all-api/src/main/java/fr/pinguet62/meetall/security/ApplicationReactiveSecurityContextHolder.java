package fr.pinguet62.meetall.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class ApplicationReactiveSecurityContextHolder {

    /**
     * Check and convert value from {@link SecurityContext#getAuthentication()} to {@link ApplicationAuthentication}.
     */
    public static Mono<ApplicationAuthentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(Objects::nonNull)
                .filter(it -> it instanceof ApplicationAuthentication)
                .map(ApplicationAuthentication.class::cast);
    }

}
