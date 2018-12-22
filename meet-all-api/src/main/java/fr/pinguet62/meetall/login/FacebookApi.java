package fr.pinguet62.meetall.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FacebookApi {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MeResponseDto {
        //private String name;
        private String id;
    }

    private final WebClient webClient;

    public FacebookApi() {
        this("https://graph.facebook.com");
    }

    // testing
    FacebookApi(String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Mono<MeResponseDto> getMe(String accessToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/me").queryParam("access_token", accessToken).build())
                .retrieve()
                .bodyToMono(MeResponseDto.class);
    }

}
