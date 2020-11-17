package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.once.dto.OnceAuthenticateFacebookRequestDto;
import fr.pinguet62.meetall.provider.once.dto.OnceAuthenticateFacebookResponseDto;
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

import javax.annotation.Nullable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
class OnceClient {

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

    /**
     * @return HTML page of nginx server.
     */
    public Mono<String> get() {
        return webClient.get()
                .uri("/")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<OnceAuthenticateFacebookResponseDto> authenticateFacebook(String facebookToken) {
        return webClient.post()
                .uri("/v2/authenticate/facebook")
                .body(fromValue(new OnceAuthenticateFacebookRequestDto(facebookToken)))
                .retrieve().bodyToMono(OnceAuthenticateFacebookResponseDto.class);
    }

    /**
     * @param size Default: {@code 10}
     */
    public Mono<OnceMatchAllResponseDto> getMatchsHistoryFiltered(String authorization, @Nullable Integer size) {
        return webClient.get()
                .uri("/v1/match/history/filtered?size={size}", size)
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
