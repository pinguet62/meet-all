package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.ExpiredTokenException;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDevicesResponseDto.HappnDevicesDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessagesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOauthResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserAcceptedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserResponseDto;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException.Gone;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnRelation.NEW_RELATION;
import static java.util.Objects.requireNonNull;

/**
 * <ol>
 * <li>https://www.facebook.com/dialog/oauth?client_id=247294518656661&redirect_uri=fbconnect://success&scope=email,user_birthday,user_friends,public_profile,user_photos,user_likes&response_type=token</li>
 * <li>Get {@code access_token} value from response https://www.facebook.com/v2.8/dialog/oauth/confirm?dpr=1</li>
 * </ol>
 */
@Component
public class HappnProviderService implements ProviderService {

    private final HappnClient client;

    public HappnProviderService(HappnClient client) {
        this.client = requireNonNull(client);
    }

    @Override
    public Provider getId() {
        return HAPPN;
    }

    @Override
    public Mono<String> loginWithFacebook(String facebookToken) {
        return client
                .connectOauthToken(facebookToken)
                .map(HappnOauthResponseDto::getAccess_token);
    }

    @Override
    public Flux<ProposalDto> getProposals(String authToken) {
        return client.getUserMe(authToken)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .map(HappnUserResponseDto::getData)
                .map(HappnUserDto::getRenewable_likes)
                .flatMapMany(limit ->
                        client.getNotifications(authToken)
                                .onErrorMap(Gone.class, ExpiredTokenException::new)
                                .flatMapIterable(HappnNotificationsResponseDto::getData)
                                .filter(it -> it.getNotifier().getMy_relation().map(myRelation -> myRelation.equals(NEW_RELATION)).orElse(false))
                                .map(HappnConverters::convert)
                                .take(limit));
    }

    @Override
    public Mono<Boolean> likeOrUnlikeProposal(String authToken, String userId, boolean likeOrUnlike) {
        Mono<HappnUserAcceptedResponseDto> acceptOrReject = likeOrUnlike ? client.acceptUser(authToken, userId) : client.rejectUser(authToken, userId);
        return acceptOrReject
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .map(HappnUserAcceptedResponseDto::getData)
                .flatMap(it -> likeOrUnlike ? Mono.just(it.isHas_crushed()) : Mono.empty());
    }

    /**
     * Ordered by {@link ConversationDto#getDate()} descending.<br>
     * Exclude first welcome conversation.<br>
     * Exclude pubs like "happn pack".
     */
    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        return client.getConversations(authToken)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .flatMapIterable(HappnConversationsResponseDto::getData)
                .filter(it -> it.getParticipants().stream().noneMatch(x -> x.getUser().getId().equals("11843")))
                .filter(it -> it.getParticipants().stream().noneMatch(x -> x.getUser().getType().equals("sponsor")))
                .map(HappnConverters::convert);
    }

    /**
     * Ordered by {@link MessageDto#getDate()} descending.
     */
    @Override
    public Flux<MessageDto> getMessages(String authToken, String conversationId) {
        return client.getMessagesForConversation(authToken, conversationId)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .flatMapIterable(HappnMessagesResponseDto::getData)
                .map(HappnConverters::convert);
    }

    @Override
    public Mono<MessageDto> sendMessage(String authToken, String conversationId, String text) {
        return client.sendMessagesToConversation(authToken, conversationId, text)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .map(HappnSendMessageResponseDto::getData)
                .map(HappnConverters::convert);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authToken, String userId) {
        return client.getUser(authToken, userId)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .map(HappnUserResponseDto::getData)
                .map(HappnConverters::convert);
    }

    @Override
    public Mono<Void> setPosition(String authToken, double latitude, double longitude, double altitude) {
        return client.getUserMe(authToken)
                .onErrorMap(Gone.class, ExpiredTokenException::new)
                .map(it -> it.getData().getId())
                .flatMapMany(meId -> client.getUserDevices(authToken, meId)
                        .flatMapIterable(it -> it.getData().orElseGet(List::of))
                        .map(HappnDevicesDto::getId)
                        .flatMap(deviceId -> client.setUserMePosition(authToken, meId, deviceId, latitude, longitude, altitude)))
                .then();
    }
}
