package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageRequestDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.ONCE;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

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
    public Mono<ProfileDto> getProfile(String authorization, String matchId) {
        return this.webClient.get()
                .uri("/v1/match/{matchId}", matchId)
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMatchResponseDto.class)
                .map(OnceMatchResponseDto::getResult)
                .map(it -> OnceConverters.convert(it.getMatch().getId(), it.getMatch().getUser(), it.getBase_url()));
    }

    @Override
    public Flux<ConversationDto> getConversations(String authorization) {
        return this.webClient.get()
                .uri("/v1/connections")
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceConversationsResponseDto.class)
                .map(OnceConversationsResponseDto::getResult)
                .flatMapIterable(result -> result.getConnections().stream().map(x -> OnceConverters.convert(x, result.getBase_url())).collect(toList()));
    }

    @Override
    public Flux<MessageDto> getMessages(String authorization, String matchId) {
        return this.webClient.get()
                .uri(uri -> uri.path("/v1/messages").queryParam("match_id", matchId).build())
                .header(HEADER, authorization)
                .retrieve().bodyToMono(OnceMessagesResponseDto.class)
                .map(OnceMessagesResponseDto::getResult)
                .flatMapIterable(result -> result.getMessages().stream().map(x -> OnceConverters.convert(x, result.getUser())).collect(toList()));
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

}
