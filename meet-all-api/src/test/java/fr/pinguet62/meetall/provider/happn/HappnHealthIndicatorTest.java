package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnOptionsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HappnHealthIndicatorTest {

    @InjectMocks
    HappnHealthIndicator healthIndicator;
    @Mock
    HappnClient client;

    @Test
    void success() {
        when(client.getOptions()).thenReturn(Mono.just(new HappnOptionsDto(false, 410, Map.of("message", "The access token provided has expired."))));
        StepVerifier.create(healthIndicator.doHealthCheck(new Builder()))
                .expectNext(new Builder().up().build())
                .verifyComplete();
    }

    @Test
    void webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.getOptions()).thenReturn(Mono.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Builder()))
                .expectNext(new Builder().down(ex).build())
                .verifyComplete();
    }
}
