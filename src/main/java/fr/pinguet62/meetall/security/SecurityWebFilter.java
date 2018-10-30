package fr.pinguet62.meetall.security;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static fr.pinguet62.meetall.security.SecurityContextHolder.withSecurityContext;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static reactor.core.publisher.Mono.justOrEmpty;

@Component
public class SecurityWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION);
        Optional<SecurityContext> context = token == null ? empty() : of(new SecurityContext(Integer.parseInt(token)));
        return chain.filter(exchange)
                .subscriberContext(withSecurityContext(justOrEmpty(context)));
    }

}
