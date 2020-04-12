package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.ApplicationAuthentication;
import fr.pinguet62.meetall.security.ApplicationReactiveSecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static fr.pinguet62.meetall.config.OpenApiConfig.BEARER;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "Credentials")
@SecurityRequirement(name = BEARER)
@RequiredArgsConstructor
@RestController
class CredentialController {

    private final CredentialService loginService;

    @Operation(summary = "List all credentials")
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
    @Operation(summary = "Register a new credential")
    @PostMapping("/credential")
    @ResponseStatus(CREATED)
    public Mono<RegisteredCredentialDto> registerCredential(
            @RequestPart @Parameter(/*FIXME*/ required = true) Provider provider,
            @RequestPart @Parameter(/*FIXME*/ required = true) String credential,
            @RequestPart @Parameter(/*FIXME*/ required = true, description = "UI information") String label) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.registerCredential(userId, provider, credential, label));
    }

    /**
     * @param id         {@link Credential#getId()}
     * @param credential {@link Credential#getCredential()}
     * @param label      {@link Credential#getLabel()}
     */
    @Operation(summary = "Update an existing credential")
    @PutMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> updateCredential(
            @PathVariable String id,
            @RequestPart(required = false) @Parameter(/*FIXME*/ required = true) String credential,
            @RequestPart(required = false) @Parameter(/*FIXME*/ required = true, description = "UI information") String label) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.updateCredential(userId, id, ofNullable(credential), ofNullable(label)));
    }

    /**
     * @param id {@link Credential#getId()}
     */
    @Operation(summary = "Delete a credential")
    @DeleteMapping("/credential/{id}")
    public Mono<RegisteredCredentialDto> deleteCredential(@PathVariable String id) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> loginService.deleteCredential(userId, id));
    }

}
