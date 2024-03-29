package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDeviceResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDeviceResponseDto.HappnDeviceDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDevicesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessagesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto.HappnNotificationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOauthResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOptionsDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageRequestDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserAcceptedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserRejectedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static fr.pinguet62.meetall.provider.happn.GraphQLUtils.parseGraph;
import static java.util.Map.entry;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
class HappnClient {

    static final String HEADER = AUTHORIZATION;

    private final WebClient webClient;

    @Autowired
    public HappnClient(WebClient.Builder webClientBuilder) {
        this(webClientBuilder, "https://api.happn.fr");
    }

    // testing
    HappnClient(WebClient.Builder webClientBuilder, String baseUrl) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<HappnOptionsDto> getOptions() {
        return webClient
                .options()
                .uri("/api")
                .retrieve()
                .bodyToMono(HappnOptionsDto.class);
    }

    public Mono<HappnOauthResponseDto> connectOauthToken(String facebookToken) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/connect/oauth/token")
                        .build())
                .body(fromFormData(
                        new LinkedMultiValueMap<>(Map.ofEntries(
                                entry("scope", List.of("mobile_app")),
                                entry("client_id", List.of("SqHSPqm6jyoFXS2sAhE6Ncc5Dvk9XQjx0mTwlwCKLt")),
                                entry("client_secret", List.of("CpGueHHxwjd6idMFbv5YOrDYCibZAb0QqbtP6LTKA1")),
                                entry("grant_type", List.of("assertion")),
                                entry("assertion_type", List.of("facebook_access_token")),
                                entry("assertion_version", List.of("6.0")),
                                entry("assertion", List.of(facebookToken))))))
                .retrieve()
                .bodyToMono(HappnOauthResponseDto.class);
    }

    public Mono<HappnNotificationsResponseDto> getNotifications(String authToken) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/me/notifications")
                        .queryParam("types", 468)
                        .queryParam("fields", parseGraph(HappnNotificationDto.class))
                        .queryParam("limit", 9999)
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnNotificationsResponseDto.class);
    }

    /**
     * @param deviceId {@link HappnDeviceDto#getId()}
     */
    public Mono<HappnRecommendationsResponseDto> getRecommendations(String authToken, String deviceId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/users/me/recommendations")
                        .queryParam("fields", parseGraph(HappnRecommendationDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .header("x-happn-did", deviceId)
                .retrieve().bodyToMono(HappnRecommendationsResponseDto.class);
    }

    public Mono<HappnUserRejectedResponseDto> rejectedUser(String authToken, String userId) {
        return acceptedOrRejectedUser(authToken, userId, "rejected")
                .bodyToMono(HappnUserRejectedResponseDto.class);
    }

    public Mono<HappnUserAcceptedResponseDto> acceptedUser(String authToken, String userId) {
        return acceptedOrRejectedUser(authToken, userId, "accepted")
                .bodyToMono(HappnUserAcceptedResponseDto.class);
    }

    private ResponseSpec acceptedOrRejectedUser(String authToken, String userId, String acceptOrReject) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/api/users/me").pathSegment(acceptOrReject).pathSegment(userId).build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve();
    }

    public Mono<HappnConversationsResponseDto> getConversations(String authToken) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/me/conversations")
                        .queryParam("fields", parseGraph(HappnConversationDto.class))
                        .queryParam("limit", 99999999)
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnConversationsResponseDto.class);
    }

    public Mono<HappnMessagesResponseDto> getConversationMessages(String authToken, String conversationId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnMessagesResponseDto.class);
    }

    public Mono<HappnSendMessageResponseDto> sendConversationMessage(String authToken, String conversationId, String text) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .body(fromValue(new HappnSendMessageRequestDto(text)))
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnSendMessageResponseDto.class);
    }

    public Mono<HappnUserResponseDto> getUserMe(String authToken) {
        return getUser(authToken, "me");
    }

    public Mono<HappnUserResponseDto> getUser(String authToken, String userId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users").pathSegment(userId)
                        .queryParam("fields", parseGraph(HappnUserDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnUserResponseDto.class);
    }

    public Mono<HappnDevicesResponseDto> getUserMeDevices(String authToken) {
        return getUserDevices(authToken, "me");
    }

    public Mono<HappnDevicesResponseDto> getUserDevices(String authToken, String userId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users").pathSegment(userId).pathSegment("devices")
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnDevicesResponseDto.class);
    }

    /**
     * @param deviceId {@link HappnDeviceDto#getId()}
     */
    public Mono<HappnDeviceResponseDto> setUserMePosition(String authToken, String deviceId, double latitude, double longitude, @Nullable Double altitude) {
        return setUserPosition(authToken, "me", deviceId, latitude, longitude, altitude);
    }

    /**
     * @param deviceId {@link HappnDeviceDto#getId()}
     */
    public Mono<HappnDeviceResponseDto> setUserPosition(String authToken, String userId, String deviceId, double latitude, double longitude, @Nullable Double altitude) {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users").pathSegment(userId).pathSegment("devices").pathSegment(deviceId)
                        .build())
                .bodyValue(Map.ofEntries(
                        Map.entry("latitude", latitude),
                        Map.entry("longitude", longitude),
                        Map.entry("alt", altitude)))
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnDeviceResponseDto.class);
    }

}
