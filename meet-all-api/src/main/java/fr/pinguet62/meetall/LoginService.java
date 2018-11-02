package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.ProviderCredentialRepository;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.exception.ConflictException;
import fr.pinguet62.meetall.exception.ForbiddenException;
import fr.pinguet62.meetall.exception.NotFoundException;
import fr.pinguet62.meetall.exception.UnauthorizedException;
import fr.pinguet62.meetall.provider.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserRepository userRepository;
    private final ProviderCredentialRepository providerCredentialRepository;

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    public Mono<String> createAccount(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("An account already exists with email " + email);
        }

        User user = new User(email, password);
        user = userRepository.save(user);

        return processLogin(user);
    }

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    public Mono<String> login(String email, String password) {
        User user = userRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new UnauthorizedException("Bad username or password"));
        return processLogin(user);
    }

    /**
     * @return The token.
     */
    private Mono<String> processLogin(User user) {
        return Mono.just(String.valueOf(user.getId()));
    }

    /**
     * @param userId {@link User#getId()}
     */
    public Flux<ProviderCredential> getRegisteredCredentials(int userId) {
        return Mono.just(userRepository.findById(userId).get())
                .flatMapIterable(User::getProviderCredentials);
    }

    /**
     * @param userId     {@link User#getId()}
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    public Mono<ProviderCredential> registerCredentials(int userId, Provider provider, String credential, String label) {
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
    public Mono<ProviderCredential> updateCredentials(int userId, int id, Optional<String> credential, Optional<String> label) {
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

}
