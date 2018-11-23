package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.ProviderCredentialRepository;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.exception.ForbiddenException;
import fr.pinguet62.meetall.exception.NotFoundException;
import fr.pinguet62.meetall.provider.Provider;
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

    /**
     * @param userId {@link User#getId()}
     */
    public Flux<ProviderCredential> getRegisteredCredentials(int userId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials);
    }

    /**
     * @param userId     {@link User#getId()}
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<ProviderCredential> registerCredential(int userId, Provider provider, String credential, String label) {
        // TODO ConflictException when provider.getMeta().get_id() already exists

        ProviderCredential providerCredential = new ProviderCredential(
                userRepository.findById(userId).get(),
                provider,
                credential,
                label
        );
        providerCredential = providerCredentialRepository.save(providerCredential);
        return Mono.just(providerCredential);
    }

    /**
     * @param userId     {@link User#getId()}
     * @param id         {@link ProviderCredential#getId()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<ProviderCredential> updateCredential(int userId, int id, Optional<String> credential, Optional<String> label) {
        ProviderCredential providerCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (providerCredential.getUser().getId() != userId) {
            throw new ForbiddenException();
        }

        credential.ifPresent(it -> providerCredential.setCredential(it));
        label.ifPresent(it -> providerCredential.setLabel(it));

        ProviderCredential updatedProviderCredential = providerCredentialRepository.save(providerCredential);

        return Mono.just(updatedProviderCredential);
    }

    /**
     * @param userId {@link User#getId()}
     * @param id     {@link ProviderCredential#getId()}
     */
    public Mono<ProviderCredential> deleteCredential(int userId, int id) {
        ProviderCredential providerCredential = providerCredentialRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ProviderCredential.class, id));

        if (providerCredential.getUser().getId() != userId) {
            throw new ForbiddenException();
        }

        providerCredentialRepository.delete(providerCredential);

        return Mono.just(providerCredential);
    }

}
