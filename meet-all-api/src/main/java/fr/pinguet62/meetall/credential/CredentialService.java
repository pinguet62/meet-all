package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.exception.ForbiddenException;
import fr.pinguet62.meetall.exception.NotFoundException;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.ProvidersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final ProvidersService providersService;

    /**
     * @param userId {@link Credential#getUserId()}
     */
    public Flux<RegisteredCredentialDto> getRegisteredCredentials(String userId) {
        return Flux.fromIterable(credentialRepository.findByUserId(userId))
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
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
                userId,
                provider,
                credential,
                label);
        createdCredential = credentialRepository.save(createdCredential);
        return Mono.just(createdCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link Credential#getUserId()}
     * @param id         {@link Credential#getId()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> updateCredential(String userId, int id, Optional<String> credential, Optional<String> label) {
        Credential updatedCredential = credentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Credential.class, id));

        if (!updatedCredential.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        credential.ifPresent(updatedCredential::setCredential);
        label.ifPresent(updatedCredential::setLabel);

        updatedCredential = credentialRepository.save(updatedCredential);

        return Mono.just(updatedCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId {@link Credential#getUserId()}
     * @param id     {@link Credential#getId()}
     */
    public Mono<RegisteredCredentialDto> deleteCredential(String userId, int id) {
        Credential deletedCredential = credentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(Credential.class, id));

        if (!deletedCredential.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        credentialRepository.delete(deletedCredential);

        return Mono.just(deletedCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

}
