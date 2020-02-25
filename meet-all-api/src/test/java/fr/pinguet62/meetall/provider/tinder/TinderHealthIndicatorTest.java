package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.tinder.dto.TinderGiphyTrendingDataResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TinderHealthIndicatorTest {

    @InjectMocks
    TinderHealthIndicator healthIndicator;
    @Mock
    TinderClient client;

    @Test
    public void test_doHealthCheck_success() {
        when(client.getGiphyTrending()).thenReturn(
                Flux.just(
                        new TinderGiphyTrendingDataResponseDto("https://giphy.com/gifs/tinder-r0e2d0n2i3t-j0qbJL2AuJcl1eq9RH"),
                        new TinderGiphyTrendingDataResponseDto("https://giphy.com/gifs/tinder-r0e2d0n2i3t-KfePgT3GUpUHAxnxtl")));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().up().build())
                .verifyComplete();
    }

    @Test
    public void test_doHealthCheck_webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.getGiphyTrending()).thenReturn(Flux.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().down(ex).build())
                .verifyComplete();
    }

}
