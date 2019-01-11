package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchByIdResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchResultMatchDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageRequestDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.ONCE;
import static fr.pinguet62.meetall.provider.once.OnceConverters.convert;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static reactor.core.publisher.Flux.fromIterable;

/**
 * Target user is identified by {@link ConversationDto#getId()},
 * because {@link #getProfile(String, String)} webservice takes this value as input.
 */
@Component
public class OnceProviderService implements ProviderService {

    static final String HEADER = AUTHORIZATION;

    private final WebClient webClient;

    public OnceProviderService() {
        this("https://onceapi.com");
    }

    // testing
    OnceProviderService(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Provider getId() {
        return ONCE;
    }

    @Override
    public Flux<ProposalDto> getProposals(String authorization) {
        return this.webClient.get()
                .uri("/v1/match")
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchAllResponseDto.class)
                .map(OnceMatchAllResponseDto::getResult)
                .flatMapMany((OnceMatchAllResultDto res) -> fromIterable(res.getMatches())
                        .map((OnceMatchResultMatchDto it) -> new ProposalDto(
                                it.getId(),
                                convert(it.getId(), it.getUser(), res.getBase_url()))));
    }

    @Override
    public Mono<Boolean> likeOrUnlikeProposal(String authorization, String matchId, boolean likeOrUnlike) {
        ResponseSpec response = this.webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/v1/match").pathSegment(matchId).pathSegment(likeOrUnlike ? "like" : "pass").build())
                .header(HEADER, authorization)
                .retrieve();
        return likeOrUnlike ?
                response.bodyToMono(OnceMatchLikeResponseDto.class)
                        .map(OnceMatchLikeResponseDto::getResult)
                        .map(OnceMatchLikeResultDto::getConnected) :
                response.bodyToMono(Void.class)
                        .map(it -> null);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authorization) {
        return this.webClient.get()
                .uri("/v1/connections")
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceConversationsResponseDto.class)
                .map(OnceConversationsResponseDto::getResult)
                .flatMapIterable(result -> result.getConnections().stream().map(x -> convert(x, result.getBase_url())).collect(toList()));
    }

    /**
     * Ordered by {@link MessageDto#getDate()} descending.<br>
     * Exclude first message "Vous avez été connectés".
     */
    @Override
    public Flux<MessageDto> getMessages(String authorization, String matchId) {
        return this.webClient.get()
                .uri(uri -> uri.path("/v1/messages").queryParam("match_id", matchId).build())
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMessagesResponseDto.class)
                .map(OnceMessagesResponseDto::getResult)
                .flatMapMany(result -> fromIterable(result.getMessages())
                        .filter(it -> it.getNumber() != 1)
                        .map(message -> convert(message, result.getUser())));
    }

    @Override
    public Mono<MessageDto> sendMessage(String authorization, String matchId, String text) {
        return this.webClient.post()
                .uri("/v1/message")
                .body(fromObject(new OnceSendMessageRequestDto(matchId, text)))
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceSendMessageResponseDto.class)
                .map(OnceSendMessageResponseDto::getResult)
                .map(OnceConverters::convertSentMessage);
    }

    @Override
    public Mono<ProfileDto> getProfile(String authorization, String matchId) {
        return this.webClient.get()
                .uri("/v1/match/{matchId}", matchId)
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchByIdResponseDto.class)
                .map(OnceMatchByIdResponseDto::getResult)
                .map(it -> convert(it.getMatch().getId(), it.getMatch().getUser(), it.getBase_url()));
    }

}
