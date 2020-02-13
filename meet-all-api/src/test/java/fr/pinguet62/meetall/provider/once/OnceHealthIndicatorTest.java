package fr.pinguet62.meetall.provider.once;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OnceHealthIndicatorTest {

    @InjectMocks
    OnceHealthIndicator healthIndicator;
    @Mock
    OnceClient client;

    @Test
    void success() {
        when(client.get()).thenReturn(Mono.just("<!DOCTYPE html> <htm> <head></heade> <body></body> </html>"));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().up().build())
                .verifyComplete();
    }

    @Test
    void webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.get()).thenReturn(Mono.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().down(ex).build())
                .verifyComplete();
    }
}
