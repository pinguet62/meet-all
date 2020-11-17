package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.ExpiredTokenException;
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
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto.OnceMatchLikeResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchResultMatchDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException.Unauthorized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

import static fr.pinguet62.meetall.provider.Provider.ONCE;
import static fr.pinguet62.meetall.provider.once.OnceConverters.convert;
import static java.util.Comparator.comparing;
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

    /**
     * Ordered by ascending {@link OnceMatchResultMatchDto#getMatch_date()}.
     */
    @Override
    public Flux<ProposalDto> getProposals(String authorization) {
        return client.getMatchsHistoryFiltered(authorization, Integer.MAX_VALUE)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceMatchAllResponseDto::getResult)
                .flatMapMany(result -> fromIterable(result.getMatches())
                        .sort(comparing(OnceMatchResultMatchDto::getMatch_date))
                        .filter(match -> !match.isLiked() && !match.isPassed())
                        .map(match -> new ProposalDto(
                                match.getId(),
                                convert(match.getId(), match.getUser(), result.getBase_url()))));
    }

    @Override
    public Mono<Void> passProposal(String authorization, String matchId) {
        return client.passMatch(authorization, matchId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .then();
    }

    @Override
    public Mono<Boolean> likeProposal(String authorization, String matchId) {
        return client.likeMatch(authorization, matchId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceMatchLikeResponseDto::getResult)
                .map(OnceMatchLikeResultDto::isConnected);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authorization) {
        return client.getConnections(authorization)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceConversationsResponseDto::getResult)
                .flatMapIterable(result -> result.getConnections().stream().map(x -> convert(x, result.getBase_url())).collect(toList()));
    }

    /**
     * Ordered by {@link MessageDto#getDate()} descending.<br>
     * Exclude first message "Vous avez été connectés".
     */
    @Override
    public Flux<MessageDto> getMessages(String authorization, String matchId) {
        return client.getMessagesForMatch(authorization, matchId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceMessagesResponseDto::getResult)
                .flatMapMany(result -> fromIterable(result.getMessages())
                        .filter(it -> it.getNumber() != 1) // TODO can be 2 when 1 is a charm
                        .map(message -> convert(message, result.getUser())));
    }

    @Override
    public Mono<MessageDto> sendMessage(String authorization, String matchId, String text) {
        return client.sendMessageToMatch(authorization, matchId, text)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceSendMessageResponseDto::getResult)
                .map(OnceConverters::convertSentMessage);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authorization, String matchId) {
        return client.getMatch(authorization, matchId)
                .onErrorMap(Unauthorized.class, ExpiredTokenException::new)
                .map(OnceMatchByIdResponseDto::getResult)
                .map(it -> convert(it.getMatch().getId(), it.getMatch().getUser(), it.getBase_url()));
    }

}
