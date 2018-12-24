package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.ProviderCredentialRepository;
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

    private final ProviderCredentialRepository providerCredentialRepository;
    private final ProvidersService providersService;

    /**
     * @param userId {@link ProviderCredential#getUserId()}
     */
    public Flux<RegisteredCredentialDto> getRegisteredCredentials(String userId) {
        return Flux.fromIterable(providerCredentialRepository.findByUserId(userId))
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link ProviderCredential#getUserId()}
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> registerCredential(String userId, Provider provider, String credential, String label) {
        // TODO ConflictException when provider.getMeta().get_id() already exists

        ProviderCredential createdProviderCredential = new ProviderCredential(
                userId,
                provider,
                credential,
                label);
        createdProviderCredential = providerCredentialRepository.save(createdProviderCredential);
        return Mono.just(createdProviderCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link ProviderCredential#getUserId()}
     * @param id         {@link ProviderCredential#getId()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> updateCredential(String userId, int id, Optional<String> credential, Optional<String> label) {
        ProviderCredential updatedProviderCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (!updatedProviderCredential.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        credential.ifPresent(updatedProviderCredential::setCredential);
        label.ifPresent(updatedProviderCredential::setLabel);

        updatedProviderCredential = providerCredentialRepository.save(updatedProviderCredential);

        return Mono.just(updatedProviderCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId {@link ProviderCredential#getUserId()}
     * @param id     {@link ProviderCredential#getId()}
     */
    public Mono<RegisteredCredentialDto> deleteCredential(String userId, int id) {
        ProviderCredential deletedProviderCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (!deletedProviderCredential.getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        providerCredentialRepository.delete(deletedProviderCredential);

        return Mono.just(deletedProviderCredential)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

}
