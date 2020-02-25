package fr.pinguet62.meetall.provider.once;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.health.AbstractReactiveHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
@Endpoint(id = "once")
public class OnceHealthIndicator extends AbstractReactiveHealthIndicator {

    private final OnceClient onceClient;

    @Override
    protected Mono<Health> doHealthCheck(Builder builder) {
        return onceClient.get()
                .map(options -> builder.up().build())
                .onErrorResume(WebClientResponseException.class, ex -> Mono.just(builder.down(ex).build()));
    }

}
