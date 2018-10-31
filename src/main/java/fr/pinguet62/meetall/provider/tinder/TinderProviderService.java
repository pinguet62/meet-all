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
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.TINDER;

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
        return webClient.get()
                .uri("/v2/matches?count=60")
                .header(HEADER, authToken)
                .retrieve().bodyToFlux(TinderGetConversationResponseDto.class)
                .map(TinderGetConversationResponseDto::getData)
                .flatMapIterable(TinderGetConversationDataResponseDto::getMatches)
                .map(TinderConverters::convert);
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

}
