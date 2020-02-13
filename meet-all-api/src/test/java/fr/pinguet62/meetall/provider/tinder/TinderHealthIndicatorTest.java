package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.tinder.dto.TinderGiphyTrendingResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGiphyTrendingResponseDto.TinderGiphyTrendingDataResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TinderHealthIndicatorTest {

    @InjectMocks
    TinderHealthIndicator healthIndicator;
    @Mock
    TinderClient client;

    @Test
    void success() {
        when(client.getGiphyTrending()).thenReturn(Mono.just(new TinderGiphyTrendingResponseDto(List.of(
                new TinderGiphyTrendingDataResponseDto("https://giphy.com/gifs/tinder-r0e2d0n2i3t-j0qbJL2AuJcl1eq9RH"),
                new TinderGiphyTrendingDataResponseDto("https://giphy.com/gifs/tinder-r0e2d0n2i3t-KfePgT3GUpUHAxnxtl")))));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().up().build())
                .verifyComplete();
    }

    @Test
    void webclientError() {
        Throwable ex = WebClientResponseException.create(500, "Internal server error.", null, null, null);
        when(client.getGiphyTrending()).thenReturn(Mono.error(ex));
        StepVerifier.create(healthIndicator.doHealthCheck(new Health.Builder()))
                .expectNext(new Health.Builder().down(ex).build())
                .verifyComplete();
    }
}
