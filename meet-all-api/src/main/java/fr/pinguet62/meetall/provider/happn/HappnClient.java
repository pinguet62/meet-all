package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessagesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOauthRequestDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOauthResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOptionsDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageRequestDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserAcceptedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.happn.GraphQLUtils.parseGraph;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
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
        return webClient.options()
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
                .body(fromValue(
                        new HappnOauthRequestDto(
                                "assertion",
                                "FUE-idSEP-f7AqCyuMcPr2K-1iCIU_YlvK-M-im3c",
                                "brGoHSwZsPjJ-lBk0HqEXVtb3UFu-y5l_JcOjD-Ekv",
                                "facebook_access_token",
                                facebookToken)))
                .retrieve()
                .bodyToMono(HappnOauthResponseDto.class);
    }

    public Mono<HappnNotificationsResponseDto> getNotifications(String authToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/me/notifications")
                        .queryParam("types", 468)
                        .queryParam("fields", parseGraph(HappnNotificationDto.class))
                        .queryParam("limit", 9999)
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnNotificationsResponseDto.class);
    }

    public Mono<HappnUserAcceptedResponseDto> acceptUser(String authToken, String userId) {
        return acceptOrRejectUser(authToken, userId, "accepted");
    }

    public Mono<HappnUserAcceptedResponseDto> rejectUser(String authToken, String userId) {
        return acceptOrRejectUser(authToken, userId, "rejected");
    }

    private Mono<HappnUserAcceptedResponseDto> acceptOrRejectUser(String authToken, String userId, String acceptOrReject) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/users/me").pathSegment(acceptOrReject).pathSegment(userId).build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnUserAcceptedResponseDto.class);
    }

    public Mono<HappnConversationsResponseDto> getConversations(String authToken) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/me/conversations")
                        .queryParam("limit", 99999999)
                        .queryParam("fields", parseGraph(HappnConversationDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnConversationsResponseDto.class);
    }

    public Mono<HappnMessagesResponseDto> getMessagesForConversation(String authToken, String conversationId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnMessagesResponseDto.class);
    }

    public Mono<HappnSendMessageResponseDto> sendMessagesToConversation(String authToken, String conversationId, String text) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .body(fromValue(new HappnSendMessageRequestDto(text)))
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnSendMessageResponseDto.class);
    }

    public Mono<HappnUserResponseDto> getUser(String authToken, String userId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users").pathSegment(userId)
                        .queryParam("fields", parseGraph(HappnUserDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnUserResponseDto.class);
    }

}
