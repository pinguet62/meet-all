package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.provider.once.dto.OnceAuthenticateFacebookResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchByIdResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.ONCE;
import static fr.pinguet62.meetall.provider.once.OnceConverters.convert;
import static java.util.stream.Collectors.toList;
import static reactor.core.publisher.Flux.fromIterable;

/**
 * Target user is identified by {@link ConversationDto#getId()},
 * because {@link #getProfile(String, String)} webservice takes this value as input.
 */
@RequiredArgsConstructor
@Component
public class OnceProviderService implements ProviderService {

    private final OnceClient client;

    @Override
    public Provider getId() {
        return ONCE;
    }

    @Override
    public Mono<String> loginWithFacebook(String facebookToken) {
        return client.authenticateFacebook(facebookToken)
                .map(OnceAuthenticateFacebookResponseDto::getToken);
    }

    @Override
    public Flux<ProposalDto> getProposals(String authorization) {
        return client.getMatchs(authorization)
                .map(OnceMatchAllResponseDto::getResult)
                .flatMapMany(result -> fromIterable(result.getMatches())
                        .filter(match -> !match.isViewed())
                        .map(match -> new ProposalDto(
                                match.getId(),
                                convert(match.getId(), match.getUser(), result.getBaseUrl()))));
    }

    @Override
    public Mono<Boolean> likeOrUnlikeProposal(String authorization, String matchId, boolean likeOrUnlike) {
        return likeOrUnlike ?
                client.likeMatch(authorization, matchId)
                        .map(OnceMatchLikeResponseDto::getResult)
                        .map(OnceMatchLikeResultDto::isConnected) :
                client.passMatch(authorization, matchId)
                        .map(it -> null);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authorization) {
        return client.getConnections(authorization)
                .map(OnceConversationsResponseDto::getResult)
                .flatMapIterable(result -> result.getConnections().stream().map(x -> convert(x, result.getBaseUrl())).collect(toList()));
    }

    /**
     * Ordered by {@link MessageDto#getDate()} descending.<br>
     * Exclude first message "Vous avez été connectés".
     */
    @Override
    public Flux<MessageDto> getMessages(String authorization, String matchId) {
        return client.getMessagesForMatch(authorization, matchId)
                .map(OnceMessagesResponseDto::getResult)
                .flatMapMany(result -> fromIterable(result.getMessages())
                        .filter(it -> it.getNumber() != 1)
                        .map(message -> convert(message, result.getUser())));
    }

    @Override
    public Mono<MessageDto> sendMessage(String authorization, String matchId, String text) {
        return client.sendMessageToMatch(authorization, matchId, text)
                .map(OnceSendMessageResponseDto::getResult)
                .map(OnceConverters::convertSentMessage);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authorization, String matchId) {
        return client.getMatch(authorization, matchId)
                .map(OnceMatchByIdResponseDto::getResult)
                .map(it -> convert(it.getMatch().getId(), it.getMatch().getUser(), it.getBaseUrl()));
    }

}
