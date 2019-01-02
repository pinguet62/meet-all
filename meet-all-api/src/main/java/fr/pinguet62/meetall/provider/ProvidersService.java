package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.PartialArrayList;
import fr.pinguet62.meetall.PartialList;
import fr.pinguet62.meetall.TransformedId;
import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.credential.CredentialRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static fr.pinguet62.meetall.PartialListUtils.concatPartialList;
import static fr.pinguet62.meetall.PartialListUtils.partialEmpty;
import static java.util.Comparator.comparing;

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
     * Update {@link ProfileDto#getId()}.
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
                                .map(it -> it.withId(TransformedId.format(providerCredential.getId(), it.getId()))));
    }

    /**
     * Merge result of each {@link ProviderService#getConversations(String)}.<br>
     * Update {@link ConversationDto#getId()}, {@link ProfileDto#getId()}, {@link MessageDto#getId()}.<br>
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
                            it = it.withProfile(it.getProfile().withId(TransformedId.format(providerCredential.getId(), it.getProfile().getId())));
                            if (it.getLastMessage() != null)
                                it = it.withLastMessage(it.getLastMessage().withId(TransformedId.format(providerCredential.getId(), it.getLastMessage().getId())));
                            return it;
                        })
                        // success or error(=partial)
                        .collect(Collectors.<ConversationDto, PartialList<ConversationDto>>toCollection(PartialArrayList::new))
                        .onErrorResume(err -> Mono.just(partialEmpty())))
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

}
