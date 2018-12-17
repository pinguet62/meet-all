package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto.TinderGetMessagesDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMatchDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageRequestDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * <ol>
 * <li>
 * Get Facebook token:
 * <ol>
 * <li>https://www.facebook.com/v2.6/dialog/oauth?redirect_uri=fb464891386855067%3A%2F%2Fauthorize%2F&scope=user_birthday,user_photos,user_education_history,email,user_relationship_details,user_friends,user_work_history,user_likes&response_type=token%2Csigned_request&client_id=464891386855067</li>
 * <li>Get {@code access_token} value from response https://www.facebook.com/v2.8/dialog/oauth/confirm?dpr=1</li>
 * </ol>
 * </li>
 *
 * <li>
 * Get Facebook user's ID
 * <ul>
 * <li>https://graph.facebook.com/me?access_token={{facebook_token}}</li>
 * <li>http://findmyfbid.com</li>
 * </ul>
 * </li>
 * </ol>
 */
@Component
public class TinderProviderService implements ProviderService {

    static final String HEADER = "x-auth-token";

    private final WebClient webClient;

    public TinderProviderService() {
        this("https://api.gotinder.com");
    }

    // for testing
    TinderProviderService(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Provider getId() {
        return TINDER;
    }

    public Mono<TinderUserDto> getMeta(String authToken) {
        return webClient.get()
                .uri("/meta")
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetMetaResponseDto.class)
                .map(TinderGetMetaResponseDto::getUser);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authToken, String userId) {
        return webClient.get()
                .uri("/user/{userId}", userId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetUserResponseDto.class)
                .map(TinderGetUserResponseDto::getResults)
                .map(TinderConverters::convert);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        Flux<TinderMatchDto> tinderMatchDtoFlux = webClient.get()
                .uri("/v2/matches?count=60")
                .header(HEADER, authToken)
                .retrieve().bodyToFlux(TinderGetConversationResponseDto.class)
                .map(TinderGetConversationResponseDto::getData)
                .flatMapIterable(TinderGetConversationDataResponseDto::getMatches);
        Mono<TinderUserDto> metaMono = getMeta(authToken);
        return metaMono.flatMapMany(me ->
                tinderMatchDtoFlux.map(tinderMessageDto ->
                        TinderConverters.convert(tinderMessageDto, me.get_id())));
    }

    @Override
    public Flux<MessageDto> getMessages(String authToken, String matchId) {
        Flux<TinderMessageDto> tinderMessageDtoFlux = webClient.get()
                .uri("/v2/matches/{matchId}/messages?count=100", matchId)
                .header(HEADER, authToken)
                .retrieve().bodyToFlux(TinderGetMessagesResponseDto.class)
                .map(TinderGetMessagesResponseDto::getData)
                .flatMapIterable(TinderGetMessagesDataResponseDto::getMessages);
        Mono<TinderUserDto> metaMono = getMeta(authToken);
        return metaMono.flatMapMany(me ->
                tinderMessageDtoFlux.map(tinderMessageDto ->
                        TinderConverters.convert(tinderMessageDto, me.get_id())));
    }

    @Override
    public Mono<MessageDto> sendMessage(String authToken, String matchId, String text) {
        return webClient.post()
                .uri("/user/matches/{matchId}", matchId)
                .body(fromObject(new TinderSendMessageRequestDto(text)))
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderSendMessageResponseDto.class)
                .map(TinderConverters::convert);
    }

}
