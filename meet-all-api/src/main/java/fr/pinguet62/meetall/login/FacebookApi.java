package fr.pinguet62.meetall.login;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.Objects.requireNonNull;

@Component
public class FacebookApi {

    @Getter
    public static class MeResponseDto {
        private final String id;

        @JsonCreator
        public MeResponseDto(@JsonProperty(value = "id", required = true) String id) {
            this.id = requireNonNull(id);
        }
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
