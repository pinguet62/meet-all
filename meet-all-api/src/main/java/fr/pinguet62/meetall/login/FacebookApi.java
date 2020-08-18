package fr.pinguet62.meetall.login;

import lombok.NonNull;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
class FacebookApi {

    @Value
    public static class MeResponseDto {
        @NonNull
        String id;
    }

    private final WebClient webClient;

    @Autowired
    public FacebookApi(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://graph.facebook.com");
    }

    // testing
    FacebookApi(WebClient.Builder webClientBuilder, String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<MeResponseDto> getMe(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/me").queryParam("access_token", accessToken).build())
                .retrieve()
                .bodyToMono(MeResponseDto.class);
    }

}
