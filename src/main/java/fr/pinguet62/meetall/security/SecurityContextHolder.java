package fr.pinguet62.meetall.security;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class SecurityContextHolder {

    private static final Class<?> SECURITY_CONTEXT_KEY = SecurityContextHolder.class;

    public static Mono<SecurityContext> getContext() {
        return Mono.subscriberContext()
                .filter(c -> c.hasKey(SECURITY_CONTEXT_KEY))
                .flatMap(c -> c.<Mono<SecurityContext>>get(SECURITY_CONTEXT_KEY));
    }

    public static Context withSecurityContext(Mono<? extends SecurityContext> securityContext) {
        return Context.of(SECURITY_CONTEXT_KEY, securityContext);
    }

}
