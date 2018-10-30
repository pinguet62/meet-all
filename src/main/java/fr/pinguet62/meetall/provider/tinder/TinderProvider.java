package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto.TinderGetMessagesDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TinderProvider implements Provider {

    private static final String HEADER = "x-auth-token";

    private static final WebClient WEB_CLIENT = WebClient.builder()
            .baseUrl("https://api.gotinder.com")
            .build();

    @Override
    public String getId() {
        return "tinder";
    }

    @Override
    public Mono<ProfileDto> getProfile(String authToken, String userId) {
        return WEB_CLIENT.get()
                .uri("/user/{userId}", userId)
                .header(HEADER, authToken)
                .retrieve().bodyToMono(TinderGetUserResponseDto.class)
                .map(TinderGetUserResponseDto::getResults)
                .map(TinderConverters::convert);
    }

    @Override
    public Flux<ConversationDto> getConversations(String authToken) {
        return WEB_CLIENT.get()
                .uri("/v2/matches?count=60")
                .header(HEADER, authToken)
                .retrieve().bodyToFlux(TinderGetConversationResponseDto.class)
                .map(TinderGetConversationResponseDto::getData)
                .flatMapIterable(TinderGetConversationDataResponseDto::getMatches)
                .map(TinderConverters::convert);
    }

    @Override
    public Flux<MessageDto> getMessages(String authToken, String matchId) {
        return WEB_CLIENT.get()
                .uri("/v2/matches/${matchId}/messages?count=100", matchId)
                .header(HEADER, authToken)
                .retrieve().bodyToFlux(TinderGetMessagesResponseDto.class)
                .map(TinderGetMessagesResponseDto::getData)
                .flatMapIterable(TinderGetMessagesDataResponseDto::getMessages)
                .map(TinderConverters::convert);
    }

}
