package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchByIdResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageRequestDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class OnceClient {

    static final String HEADER = AUTHORIZATION;

    private final WebClient webClient;

    @Autowired
    public OnceClient(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://onceapi.com");
    }

    // testing
    OnceClient(WebClient.Builder webClientBuilder, String baseUrl) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<OnceMatchAllResponseDto> getMatchs(String authorization) {
        return webClient.get()
                .uri("/v1/match")
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchAllResponseDto.class);
    }

    public Mono<OnceMatchLikeResponseDto> likeMatch(String authorization, String matchId) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/match").pathSegment(matchId).pathSegment("like").build())
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchLikeResponseDto.class);
    }

    public Mono<Void> passMatch(String authorization, String matchId) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/match").pathSegment(matchId).pathSegment("pass").build())
                .header(HEADER, authorization)
                .retrieve().bodyToMono(Void.class);
    }

    public Mono<OnceConversationsResponseDto> getConnections(String authorization) {
        return webClient.get()
                .uri("/v1/connections")
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceConversationsResponseDto.class);
    }

    public Mono<OnceMessagesResponseDto> getMessagesForMatch(String authorization, String matchId) {
        return webClient.get()
                .uri(uri -> uri.path("/v1/messages").queryParam("match_id", matchId).build())
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMessagesResponseDto.class);
    }

    public Mono<OnceSendMessageResponseDto> sendMessageToMatch(String authorization, String matchId, String text) {
        return webClient.post()
                .uri("/v1/message")
                .body(fromValue(new OnceSendMessageRequestDto(matchId, text)))
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceSendMessageResponseDto.class);
    }

    public Mono<OnceMatchByIdResponseDto> getMatch(String authorization, String matchId) {
        return webClient.get()
                .uri("/v1/match/{matchId}", matchId)
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchByIdResponseDto.class);
    }

}
