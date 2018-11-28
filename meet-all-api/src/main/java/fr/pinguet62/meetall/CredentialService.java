package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.ProviderCredentialRepository;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
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

    private final UserRepository userRepository;
    private final ProviderCredentialRepository providerCredentialRepository;
    private final ProvidersService providersService;

    /**
     * @param userId {@link User#getId()}
     */
    public Flux<RegisteredCredentialDto> getRegisteredCredentials(int userId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .flatMap(providerCredential ->
                        providersService.getProviderService(providerCredential.getProvider())
                                .checkCredential(providerCredential.getCredential())
                                .map(ok -> new RegisteredCredentialDto(providerCredential.getId(), providerCredential.getLabel(), providerCredential.getProvider(), ok)));
    }

    /**
     * @param userId     {@link User#getId()}
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> registerCredential(int userId, Provider provider, String credential, String label) {
        // TODO ConflictException when provider.getMeta().get_id() already exists

        ProviderCredential createdProviderCredential = new ProviderCredential(
                userRepository.findById(userId).get(),
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
     * @param userId     {@link User#getId()}
     * @param id         {@link ProviderCredential#getId()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<RegisteredCredentialDto> updateCredential(int userId, int id, Optional<String> credential, Optional<String> label) {
        ProviderCredential updatedProviderCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (updatedProviderCredential.getUser().getId() != userId) {
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
     * @param userId {@link User#getId()}
     * @param id     {@link ProviderCredential#getId()}
     */
    public Mono<RegisteredCredentialDto> deleteCredential(int userId, int id) {
        ProviderCredential deletedProviderCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (deletedProviderCredential.getUser().getId() != userId) {
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
