package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.ExpiredTokenException;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookResponseDto.TinderAuthLoginFacebookResponseDataDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto.TinderGetMessagesDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderGetRecommendationsDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto.TinderProfileDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto.TinderProfileDto.TinderProfileLikesDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.time.Clock;

import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static fr.pinguet62.meetall.provider.tinder.TinderConverters.convert;

/**
 * <ol>
 * <li>
 * Get Facebook token:
 * <ol>
 * <li>https://www.facebook.com/v2.6/dialog/oauth?redirect_uri=fb464891386855067%3A%2F%2Fauthorize%2F&scope=user_birthday,user_photos,user_education_history,email,user_relationship_details,user_friends,user_work_history,user_likes&response_type=token%2Csigned_request&client_id=464891386855067</li>
 * <li>Get {@code access_token} value from response https://www.facebook.com/v2.12/dialog/oauth/confirm?dpr=1</li>
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
@RequiredArgsConstructor
@Component
public class TinderProviderService implements ProviderService {

    private final Clock clock;
    private final TinderClient client;

    @Override
    public Provider getId() {
        return TINDER;
    }

    @Override
    public Mono<String> loginWithFacebook(String facebookAccessToken) {
        return client.authLoginFacebook(facebookAccessToken)
                .map(TinderAuthLoginFacebookResponseDto::getData)
                .map(TinderAuthLoginFacebookResponseDataDto::getApi_token);
    }

    @Override
    public Flux<ProposalDto> getProposals(String authToken) {
        return client.getProfile(authToken)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderProfileResponseDto::getData)
                .map(TinderProfileDto::getLikes)
                .map(TinderProfileLikesDto::getLikes_remaining)
                .flatMapMany(limit ->
                        client.getRecommendations(authToken)
                                .map(TinderGetRecommendationsResponseDto::getData)
                                .flatMapIterable(TinderGetRecommendationsDataResponseDto::getResults)
                                .map(it -> TinderConverters.convert(it, clock))
                                .take(limit));
    }

    @Override
    public Mono<Void> passProposal(String authToken, String userId) {
        return client.passUser(authToken, userId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .then();
    }

    @Override
    public Mono<Boolean> likeProposal(String authToken, String userId) {
        return client.likeUser(authToken, userId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderConverters::convert);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        return getMetaUser(authToken)
                .flatMapMany(me ->
                        client.getMatches(authToken)
                                .map(TinderGetConversationResponseDto::getData)
                                .flatMapIterable(TinderGetConversationDataResponseDto::getMatches)
                                .map(tinderMessageDto -> convert(tinderMessageDto, me.get_id(), clock)));
    }

    @Override
    public Flux<MessageDto> getMessages(String authToken, String matchId) {
        return getMetaUser(authToken)
                .flatMapMany(me ->
                        client.getMessagesForMatch(authToken, matchId)
                                .map(TinderGetMessagesResponseDto::getData)
                                .flatMapIterable(TinderGetMessagesDataResponseDto::getMessages)
                                .map(tinderMessageDto -> convert(tinderMessageDto, me.get_id())));
    }

    @Override
    public Mono<MessageDto> sendMessage(String authToken, String matchId, String text) {
        return client.sendMessageToMatch(authToken, matchId, text)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderConverters::convert);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authToken, String userId) {
        return client.getUser(authToken, userId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderGetUserResponseDto::getResults)
                .map(it -> TinderConverters.convert(it, clock));
    }

    private Mono<TinderUserDto> getMetaUser(String authToken) {
        return client.getMeta(authToken)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderGetMetaResponseDto::getUser);
    }

    @Override
    public Mono<Void> setPosition(String authToken, double latitude, double longitude, @Nullable Double altitude) {
        return client.ping(authToken, latitude, longitude)
                .then();
    }
}
