package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.exception.ConflictException;
import fr.pinguet62.meetall.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Transactional
public class LoginService {

    private final UserRepository userRepository;

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

}
