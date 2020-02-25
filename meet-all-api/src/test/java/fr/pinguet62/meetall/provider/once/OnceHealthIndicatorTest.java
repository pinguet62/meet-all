package fr.pinguet62.meetall.provider.once;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OnceHealthIndicatorTest {

    @InjectMocks
    OnceHealthIndicator healthIndicator;
    @Mock
    OnceClient client;

    @Test
    public void test_doHealthCheck_success() {
        when(client.get()).thenReturn(Mono.just("<!DOCTYPE html> <htm> <head></heade> <body></body> </html>"));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().up().build())
                .verifyComplete();
    }

    @Test
    public void test_doHealthCheck_webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.get()).thenReturn(Mono.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().down(ex).build())
                .verifyComplete();
    }

}
