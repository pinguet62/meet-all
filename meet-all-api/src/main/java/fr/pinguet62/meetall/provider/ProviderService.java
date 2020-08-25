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

    Mono<Boolean> likeOrUnlikeProposal(String credential, String proposalId, boolean likeOrUnlike);

    Flux<ConversationDto> getConversations(String credential);

    Flux<MessageDto> getMessages(String credential, String conversationId);

    Mono<MessageDto> sendMessage(String credential, String conversationId, String text);

    Mono<ProfileDto> getProfile(String credential, String profileId);

}
