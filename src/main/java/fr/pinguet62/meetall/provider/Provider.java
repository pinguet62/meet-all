package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface Provider {

    String getId();

    Mono<ProfileDto> getProfile(String credentials, String profileId);

    Flux<ConversationDto> getConversations(String credentials);

    Flux<MessageDto> getMessages(String credentials, String conversationId);

}
