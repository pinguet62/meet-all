package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.PartialArrayList;
import fr.pinguet62.meetall.PartialList;
import fr.pinguet62.meetall.TransformedId;
import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.credential.CredentialRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.exception.ExpiredTokenException;
import fr.pinguet62.meetall.photoproxy.PhotoProxyEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static fr.pinguet62.meetall.PartialListUtils.concatPartialList;
import static fr.pinguet62.meetall.PartialListUtils.partialEmpty;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
public class ProvidersService {

    private final CredentialRepository credentialRepository;
    private final List<ProviderService> providerServices;

    /**
     * @see ProviderService#getId()
     */
    public ProviderService getProviderService(Provider provider) {
        return providerServices.stream().filter(p -> p.getId().equals(provider)).findFirst()
                .orElseThrow(() -> new NoProviderFoundException(provider));
    }

    /**
     * Update {@link ProposalDto#getId()}, {@link ProfileDto#getId()}, {@link ProfileDto#getAvatars()}.
     *
     * @param userId {@link Credential#getUserId()}
     */
    public Mono<PartialList<ProposalDto>> getProposalsForUser(String userId) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .flatMap(providerCredential -> getProviderService(providerCredential.getProvider())
                        .getProposals(providerCredential.getCredential())
                        .map(it -> it
                                .withId(TransformedId.format(providerCredential.getId(), it.getId()))
                                .withProfile(it.getProfile()
                                        .withId(TransformedId.format(providerCredential.getId(), it.getProfile().getId()))
                                        .withAvatars(it.getProfile().getAvatars().stream().map(PhotoProxyEncoder::encode).collect(toList()))))
                        // success or error(=partial)
                        .collect(Collectors.<ProposalDto, PartialList<ProposalDto>>toCollection(PartialArrayList::new))
                        .onErrorReturn(ExpiredTokenException.class, partialEmpty()))
                .reduce(concatPartialList());
    }

    public Mono<Boolean> likeOrUnlikeProposal(String userId, int credentialId, String proposalId, boolean likeOrUnlike) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .filter(providerCredential -> providerCredential.getId().equals(credentialId))
                .next()
                .flatMap(providerCredential -> getProviderService(providerCredential.getProvider()).likeOrUnlikeProposal(providerCredential.getCredential(), proposalId, likeOrUnlike));
    }

    /**
     * Merge result of each {@link ProviderService#getConversations(String)}.<br>
     * Update {@link ConversationDto#getId()}, {@link ProfileDto#getId()}, {@link ProfileDto#getAvatars()}, {@link MessageDto#getId()}.<br>
     * Order by descending {@link ConversationDto#getDate()}.
     *
     * @param userId {@link Credential#getUserId()}
     */
    public Mono<PartialList<ConversationDto>> getConversationsForUser(String userId) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .flatMap(providerCredential -> getProviderService(providerCredential.getProvider())
                        .getConversations(providerCredential.getCredential())
                        .map(it -> {
                            it = it.withId(TransformedId.format(providerCredential.getId(), it.getId()));
                            it = it.withProfile(it.getProfile()
                                    .withId(TransformedId.format(providerCredential.getId(), it.getProfile().getId()))
                                    .withAvatars(it.getProfile().getAvatars().stream().map(PhotoProxyEncoder::encode).collect(toList())));
                            if (it.getLastMessage().isPresent())
                                it = it.withLastMessage(it.getLastMessage().get()
                                        .withId(TransformedId.format(providerCredential.getId(), it.getLastMessage().get().getId())));
                            return it;
                        })
                        // success or error(=partial)
                        .collect(Collectors.<ConversationDto, PartialList<ConversationDto>>toCollection(PartialArrayList::new))
                        .onErrorReturn(ExpiredTokenException.class, partialEmpty()))
                .reduce(concatPartialList())
                .doOnNext(it -> it.sort(comparing(ConversationDto::getDate).reversed()));
    }

    /**
     * Update {@link MessageDto#getId()}.<br>
     * Order by ascending {@link MessageDto#getDate()}.
     *
     * @param userId         {@link Credential#getUserId()}
     * @param credentialId   {@link Credential#getId()}
     * @param conversationId {@link ConversationDto#getId()}
     */
    public Flux<MessageDto> getMessagesForUser(String userId, int credentialId, String conversationId) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .filter(providerCredential -> providerCredential.getId().equals(credentialId))
                .next()
                .flatMapMany(providerCredential ->
                        getProviderService(providerCredential.getProvider()).getMessages(providerCredential.getCredential(), conversationId)
                                .map(it -> it.withId(TransformedId.format(providerCredential.getId(), it.getId()))))
                .sort(comparing(MessageDto::getDate));
    }

    /**
     * Update {@link MessageDto#getId()}.
     *
     * @param userId         {@link Credential#getUserId()}
     * @param credentialId   {@link Credential#getId()}
     * @param conversationId {@link ConversationDto#getId()}
     * @param text           {@link MessageDto#getText()}
     */
    public Mono<MessageDto> sendMessage(String userId, int credentialId, String conversationId, String text) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .filter(providerCredential -> providerCredential.getId().equals(credentialId))
                .next()
                .flatMap(providerCredential ->
                        getProviderService(providerCredential.getProvider()).sendMessage(providerCredential.getCredential(), conversationId, text)
                                .map(it -> it.withId(TransformedId.format(providerCredential.getId(), it.getId()))));
    }

    /**
     * Update {@link ProfileDto#getId()}, {@link ProfileDto#getAvatars()}.
     *
     * @param userId       {@link Credential#getUserId()}
     * @param credentialId {@link Credential#getId()}
     * @param profileId    {@link ProfileDto#getId()}
     */
    public Mono<ProfileDto> getProfileForUser(String userId, int credentialId, String profileId) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .filter(providerCredential -> providerCredential.getId().equals(credentialId))
                .next()
                .flatMap(providerCredential ->
                        getProviderService(providerCredential.getProvider()).getProfile(providerCredential.getCredential(), profileId)
                                .map(it -> it
                                        .withId(TransformedId.format(providerCredential.getId(), it.getId()))
                                        .withAvatars(it.getAvatars().stream().map(PhotoProxyEncoder::encode).collect(toList()))));
    }

}
