package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnOptionsDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health.Builder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HappnHealthIndicatorTest {

    @InjectMocks
    HappnHealthIndicator healthIndicator;
    @Mock
    HappnClient client;

    @Test
    public void test_doHealthCheck_success() {
        when(client.getOptions()).thenReturn(Mono.just(new HappnOptionsDto(false, 410, Map.of("message", "The access token provided has expired."))));
        StepVerifier.create(healthIndicator.doHealthCheck(new Builder()))
                .expectNext(new Builder().up().build())
                .verifyComplete();
    }

    @Test
    public void test_doHealthCheck_webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.getOptions()).thenReturn(Mono.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Builder()))
                .expectNext(new Builder().down(ex).build())
                .verifyComplete();
    }

}
