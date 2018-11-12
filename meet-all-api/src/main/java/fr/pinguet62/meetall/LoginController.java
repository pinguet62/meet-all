package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.exception.UnauthorizedException;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.SecurityContext;
import fr.pinguet62.meetall.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;
import static reactor.core.publisher.Mono.error;

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
    public Mono<String> createAccount(@RequestPart String email, @RequestPart String password) {
        return loginService.createAccount(email, password);
    }

    /**
     * @param email    {@link User#getEmail()}
     * @param password {@link User#getPassword()}
     * @return The token.
     */
    @PostMapping("/login")
    public Mono<String> login(@RequestPart String email, @RequestPart String password) {
        return loginService.login(email, password);
    }

    @GetMapping("/credential")
    public Flux<RegisteredCredentialDto> getRegisteredCredentials() {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMapMany(loginService::getRegisteredCredentials)
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

    /**
     * @param provider   {@link ProviderCredential#getProvider()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    @PostMapping("/credential")
    @ResponseStatus(CREATED)
    public Mono<RegisteredCredentialDto> registerCredential(@RequestPart Provider provider, @RequestPart String credential, @RequestPart String label) {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> loginService.registerCredential(userId, provider, credential, label))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

    /**
     * @param id         {@link ProviderCredential#getId()}
     * @param credential {@link ProviderCredential#getCredential()}
     * @param label      {@link ProviderCredential#getLabel()}
     */
    @PutMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> updateCredential(@PathVariable int id, @RequestPart(required = false) String credential, @RequestPart(required = false) String label) {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> loginService.updateCredential(userId, id, ofNullable(credential), ofNullable(label)))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

    /**
     * @param id {@link ProviderCredential#getId()}
     */
    @DeleteMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> deleteCredential(@PathVariable int id) {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> loginService.deleteCredential(userId, id))
                .map(entity -> new RegisteredCredentialDto(entity.getId(), entity.getLabel(), entity.getProvider()));
    }

}
