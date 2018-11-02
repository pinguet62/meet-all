package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.SecurityContext;
import fr.pinguet62.meetall.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    @PostMapping("/user")
    @ResponseStatus(CREATED)
    public Mono<String> createAccount(@RequestParam String email, @RequestParam String password) {
        return loginService.createAccount(email, password);
    }

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    @PostMapping("/login")
    public Mono<String> login(@RequestParam String email, @RequestParam String password) {
        return loginService.login(email, password);
    }

    @GetMapping("/credential")
    public Flux<RegisteredCredentialDto> getRegisteredCredentials() {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .doOnNext(it -> System.out.println(it))
                .flatMapMany(userId -> loginService.getRegisteredCredentials(userId))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

    /**
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    @PostMapping("/credential")
    @ResponseStatus(CREATED)
    public Mono<RegisteredCredentialDto> registerCredentials(@RequestParam Provider provider, @RequestParam String credential, @RequestParam String label) {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .flatMap(userId -> loginService.registerCredentials(userId, provider, credential, label))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

    /**
     * @param id         {@link ProviderCredential#getId()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    @PutMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> updateCredentials(@PathVariable int id, @RequestParam(required = false) String credential, @RequestParam(required = false) String label) {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .flatMap(userId -> loginService.updateCredentials(userId, id, ofNullable(credential), ofNullable(label)))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

}
