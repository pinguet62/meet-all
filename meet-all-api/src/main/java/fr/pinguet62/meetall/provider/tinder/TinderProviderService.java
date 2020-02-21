package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.ExpiredTokenException;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto.TinderGetMessagesDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderGetRecommendationsDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileLikesDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static fr.pinguet62.meetall.provider.tinder.TinderConverters.convert;
import static java.util.Objects.requireNonNull;

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

    private final TinderClient client;

    TinderProviderService(TinderClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    public Provider getId() {
        return TINDER;
    }

    /**
     * Count limited by {@code "data.likes.likes_remaining"} from {@code "/profile"} result.
     */
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
                                .map(TinderConverters::convert)
                                .take(limit));
    }

    /**
     * @throws RuntimeException When {@code "data.likes.likes_remaining"} is {@code 0}.
     */
    @Override
    public Mono<Boolean> likeOrUnlikeProposal(String authToken, String userId, boolean likeOrUnlike) {
        Mono<TinderLikeResponseDto> likeOrPass = likeOrUnlike ? client.likeUser(authToken, userId) : client.passUser(authToken, userId);
        return likeOrPass
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .flatMap(it -> it.getRate_limited_until() != null ? Mono.error(new RuntimeException()) : Mono.just(it)) // "likes remaining" support
                .flatMap(it -> likeOrUnlike ? Mono.just(convert(it)) : Mono.empty());
    }

    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        return getMetaUser(authToken)
                .flatMapMany(me ->
                        client.getMatches(authToken)
                                .map(TinderGetConversationResponseDto::getData)
                                .flatMapIterable(TinderGetConversationDataResponseDto::getMatches)
                                .map(tinderMessageDto -> convert(tinderMessageDto, me.get_id())));
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
                .map(TinderConverters::convert);
    }

    private Mono<TinderUserDto> getMetaUser(String authToken) {
        return client.getMeta(authToken)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(TinderGetMetaResponseDto::getUser);
    }

}
