package fr.pinguet62.meetall.provider.happn;

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
@Endpoint(id = "happn")
public class HappnHealthIndicator extends AbstractReactiveHealthIndicator {

    private final HappnClient happnClient;

    @Override
    protected Mono<Health> doHealthCheck(Builder builder) {
        return happnClient.getOptions()
                .map(options -> builder.up().build())
                .onErrorResume(WebClientResponseException.class, ex -> Mono.just(builder.down(ex).build()));
    }

}
