package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessagesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageRequestDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.happn.GraphQLUtils.parseGraph;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * <ol>
 * <li>https://www.facebook.com/dialog/oauth?client_id=247294518656661&redirect_uri=fbconnect://success&scope=email,user_birthday,user_friends,public_profile,user_photos,user_likes&response_type=token</li>
 * <li>Get {@code access_token} value from response https://www.facebook.com/v2.8/dialog/oauth/confirm?dpr=1</li>
 * </ol>
 */
@Component
public class HappnProviderService implements ProviderService {

    static final String HEADER = AUTHORIZATION;

    private final WebClient webClient;

    public HappnProviderService() {
        this("https://api.happn.fr/api");
    }

    // testing
    HappnProviderService(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Provider getId() {
        return HAPPN;
    }

    @Override
    public Mono<ProfileDto> getProfile(String authToken, String profileId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("users").pathSegment(profileId)
                        .queryParam("fields", parseGraph(HappnUserDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnUserResponseDto.class)
                .map(HappnUserResponseDto::getData)
                .map(HappnConverters::convert);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/me/conversations")
                        .queryParam("limit", 99999999)
                        .queryParam("fields", parseGraph(HappnConversationDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnConversationsResponseDto.class)
                .flatMapIterable(HappnConversationsResponseDto::getData)
                .map(HappnConverters::convert);
    }

    /**
     * Ordered by {@link MessageDto#getDate()} descending.
     */
    @Override
    public Flux<MessageDto> getMessages(String authToken, String conversationId) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnMessagesResponseDto.class)
                .flatMapIterable(HappnMessagesResponseDto::getData)
                .map(HappnConverters::convert);
    }

    @Override
    public Mono<MessageDto> sendMessage(String authToken, String conversationId, String text) {
        return this.webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("conversations").pathSegment(conversationId).pathSegment("messages")
                        .queryParam("fields", parseGraph(HappnMessageDto.class))
                        .build())
                .body(fromObject(new HappnSendMessageRequestDto(text)))
                .header(HEADER, "OAuth=\"" + authToken + "\"")
                .retrieve().bodyToMono(HappnSendMessageResponseDto.class)
                .map(HappnSendMessageResponseDto::getData)
                .map(HappnConverters::convert);
    }

}
