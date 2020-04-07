package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProviderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final ProviderFactory providerFactory;

    public Flux<Credential> findByUserId(String userId) {
        return credentialRepository.findByUserId(userId);
    }

    /**
     * @param userId {@link Credential#getUserId()}
     */
    public Flux<RegisteredCredentialDto> getRegisteredCredentials(String userId) {
        return credentialRepository.findByUserId(userId)
                .flatMap(providerCredential ->
                        providerFactory.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link Credential#getUserId()}
     * @param provider   {@link Credential#getProvider()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> registerCredential(String userId, Provider provider, String credential, String label) {
        // TODO ConflictException when provider.getMeta().get_id() already exists

        Credential createdCredential = new Credential(
                null, // @Id generated
                userId,
                provider,
                credential,
                label);
        return credentialRepository.save(createdCredential)
                .flatMap(providerCredential ->
                        providerFactory.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link Credential#getUserId()}
     * @param id         {@link Credential#getId()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> updateCredential(String userId, String id, Optional<String> credential, Optional<String> label) {
        return credentialRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(Credential.class, id)))
                .doOnNext(updatedCredential -> {
                    if (!updatedCredential.getUserId().equals(userId)) {
                        throw new ForbiddenException();
                    }
                })
                .map(it -> {
                    credential.ifPresent(it::withCredential);
                    label.ifPresent(it::withLabel);
                    return it;
                })
                .flatMap(credentialRepository::save)
                .flatMap(providerCredential ->
                        providerFactory.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId {@link Credential#getUserId()}
     * @param id     {@link Credential#getId()}
     */
    public Mono<RegisteredCredentialDto> deleteCredential(String userId, String id) {
        return credentialRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(Credential.class, id)))
                .doOnNext(updatedCredential -> {
                    if (!updatedCredential.getUserId().equals(userId)) {
                        throw new ForbiddenException();
                    }
                })
                .flatMap(deletedCredential ->
                        credentialRepository.delete(deletedCredential)
                                .then(Mono.defer(() ->
                                        providerFactory.getProviderService(deletedCredential.getProvider())
                                                .checkCredential(deletedCredential.getCredential())
                                                .map(ok -> new RegisteredCredentialDto(deletedCredential.getId(), deletedCredential.getLabel(), deletedCredential.getProvider(), ok)))));
    }

}
