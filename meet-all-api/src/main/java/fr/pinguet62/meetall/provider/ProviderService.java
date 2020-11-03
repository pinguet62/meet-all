package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.ExpiredTokenException;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProviderService {

    Provider getId();

    /**
     * @return The credential.
     */
    Mono<String> loginWithFacebook(String facebookToken);

    default Mono<Boolean> checkCredential(String credential) {
        return getConversations(credential) // any call
                .collectList().map(it -> true)
                .onErrorReturn(ExpiredTokenException.class, false);
    }

    Flux<ProposalDto> getProposals(String credential);

    Mono<Void> passProposal(String credential, String proposalId);

    Mono<Boolean> likeProposal(String credential, String proposalId);

    Flux<ConversationDto> getConversations(String credential);

    Flux<MessageDto> getMessages(String credential, String conversationId);

    Mono<MessageDto> sendMessage(String credential, String conversationId, String text);

    Mono<ProfileDto> getProfile(String credential, String profileId);

    /**
     * @return {@link Mono#empty()} when not supported or success.
     */
    default Mono<Void> setPosition(String credential, double latitude, double longitude, double altitude) {
        return Mono.empty();
    }

}
