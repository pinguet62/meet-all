package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageRequestDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class TinderClient {

    static final String HEADER = "x-auth-token";

    private final WebClient webClient;

    @Autowired
    public TinderClient(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://api.gotinder.com");
    }

    // testing
    TinderClient(WebClient.Builder webClientBuilder, String baseUrl) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<TinderGetRecommendationsResponseDto> getRecommendations(String authToken) {
        return webClient.get()
                .uri("/v2/recs/core")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetRecommendationsResponseDto.class);
    }

    public Mono<TinderLikeResponseDto> likeUser(String authToken, String userId) {
        return likeOrPassUser(authToken, userId, "like");
    }

    public Mono<TinderLikeResponseDto> passUser(String authToken, String userId) {
        return likeOrPassUser(authToken, userId, "pass");
    }

    private Mono<TinderLikeResponseDto> likeOrPassUser(String authToken, String userId, String likeOrPass) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(likeOrPass).pathSegment(userId).build())
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderLikeResponseDto.class);
    }

    public Mono<TinderGetConversationResponseDto> getMatches(String authToken) {
        return webClient.get()
                .uri("/v2/matches?count=60")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetConversationResponseDto.class);
    }

    public Mono<TinderGetMessagesResponseDto> getMessagesForMatch(String authToken, String matchId) {
        return webClient.get()
                .uri("/v2/matches/{matchId}/messages?count=100", matchId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetMessagesResponseDto.class);
    }

    public Mono<TinderSendMessageResponseDto> sendMessageToMatch(String authToken, String matchId, String text) {
        return webClient.post()
                .uri("/user/matches/{matchId}", matchId)
                .body(fromValue(new TinderSendMessageRequestDto(text)))
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderSendMessageResponseDto.class);
    }

    public Mono<TinderGetUserResponseDto> getUser(String authToken, String userId) {
        return webClient.get()
                .uri("/user/{userId}", userId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetUserResponseDto.class);
    }

    public Mono<TinderProfileResponseDto> getProfile(String authToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/profile")
                        .queryParam("include", "likes")
                        .build())
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderProfileResponseDto.class);
    }

    public Mono<TinderGetMetaResponseDto> getMeta(String authToken) {
        return webClient.get()
                .uri("/meta")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetMetaResponseDto.class);
    }

}
