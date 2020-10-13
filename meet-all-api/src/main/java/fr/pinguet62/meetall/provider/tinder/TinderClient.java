package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookRequestDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGiphyTrendingResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderPingResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageRequestDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUnlikeResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
class TinderClient {

    static final String HEADER = "x-auth-token";

    private final WebClient webClient;

    @Autowired
    TinderClient(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://api.gotinder.com");
    }

    // testing
    TinderClient(WebClient.Builder webClientBuilder, String baseUrl) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    Mono<TinderAuthLoginFacebookResponseDto> authLoginFacebook(String facebookToken) {
        return webClient.post()
                .uri("/v2/auth/login/facebook")
                .body(fromValue(new TinderAuthLoginFacebookRequestDto(facebookToken)))
                .retrieve().bodyToMono(TinderAuthLoginFacebookResponseDto.class);
    }

    Mono<TinderGetRecommendationsResponseDto> getRecommendations(String authToken) {
        return webClient.get()
                .uri("/v2/recs/core")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetRecommendationsResponseDto.class);
    }

    Mono<TinderLikeResponseDto> likeUser(String authToken, String userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("like").pathSegment(userId).build())
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderLikeResponseDto.class);
    }

    Mono<TinderUnlikeResponseDto> passUser(String authToken, String userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("pass").pathSegment(userId).build())
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderUnlikeResponseDto.class);
    }

    Mono<TinderGetConversationResponseDto> getMatches(String authToken) {
        return webClient.get()
                .uri("/v2/matches?count=60")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetConversationResponseDto.class);
    }

    Mono<TinderGetMessagesResponseDto> getMessagesForMatch(String authToken, String matchId) {
        return webClient.get()
                .uri("/v2/matches/{matchId}/messages?count=100", matchId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetMessagesResponseDto.class);
    }

    Mono<TinderSendMessageResponseDto> sendMessageToMatch(String authToken, String matchId, String text) {
        return webClient.post()
                .uri("/user/matches/{matchId}", matchId)
                .body(fromValue(new TinderSendMessageRequestDto(text)))
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderSendMessageResponseDto.class);
    }

    Mono<TinderGetUserResponseDto> getUser(String authToken, String userId) {
        return webClient.get()
                .uri("/user/{userId}", userId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetUserResponseDto.class);
    }

    Mono<TinderProfileResponseDto> getProfile(String authToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/profile")
                        .queryParam("include", "likes")
                        .build())
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderProfileResponseDto.class);
    }

    Mono<TinderGetMetaResponseDto> getMeta(String authToken) {
        return webClient.get()
                .uri("/meta")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetMetaResponseDto.class);
    }

    Mono<TinderPingResponseDto> ping(String authToken, double lat, double lon) {
        return webClient.post()
                .uri("/user/ping")
                .bodyValue(Map.ofEntries(
                        Map.entry("lat", lat),
                        Map.entry("lon", lon)))
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderPingResponseDto.class);
    }

    Mono<TinderGiphyTrendingResponseDto> getGiphyTrending() {
        return webClient.get()
                .uri("/giphy/trending")
                .retrieve().bodyToMono(TinderGiphyTrendingResponseDto.class);
    }
}
