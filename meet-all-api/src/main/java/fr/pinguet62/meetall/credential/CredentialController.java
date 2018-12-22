package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.ApplicationAuthentication;
import fr.pinguet62.meetall.security.ApplicationReactiveSecurityContextHolder;
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

@RequiredArgsConstructor
@RestController
public class CredentialController {

    private final CredentialService loginService;

    @GetMapping("/credential")
    public Flux<RegisteredCredentialDto> getRegisteredCredentials() {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMapMany(loginService::getRegisteredCredentials);
    }

    /**
     * @param provider   {@link Credential#getProvider()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    @PostMapping("/credential")
    @ResponseStatus(CREATED)
    public Mono<RegisteredCredentialDto> registerCredential(@RequestPart Provider provider, @RequestPart String credential, @RequestPart String label) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.registerCredential(userId, provider, credential, label));
    }

    /**
     * @param id         {@link Credential#getId()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    @PutMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> updateCredential(@PathVariable int id, @RequestPart(required = false) String credential, @RequestPart(required = false) String label) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.updateCredential(userId, id, ofNullable(credential), ofNullable(label)));
    }

    /**
     * @param id {@link Credential#getId()}
     */
    @DeleteMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> deleteCredential(@PathVariable int id) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.deleteCredential(userId, id));
    }

}
