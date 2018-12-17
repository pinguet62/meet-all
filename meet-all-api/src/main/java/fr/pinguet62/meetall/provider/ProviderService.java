package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProviderService {

    Provider getId();

    default Mono<Boolean> checkCredential(String credential) {
        return getConversations(credential)
                .collectList().map(it -> true)
                .onErrorReturn(false);
    }

    Mono<ProfileDto> getProfile(String credential, String profileId);

    Flux<ConversationDto> getConversations(String credential);

    Flux<MessageDto> getMessages(String credential, String conversationId);

    Mono<MessageDto> sendMessage(String credential, String conversationId, String text);

}
