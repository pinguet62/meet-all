package fr.pinguet62.meetall.facebookcredential;

import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.credential.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.ApplicationAuthentication;
import fr.pinguet62.meetall.security.ApplicationReactiveSecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.config.OpenApiConfig.BEARER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Tag(name = "Credentials from Facebook")
@SecurityRequirement(name = BEARER)
@RequiredArgsConstructor
@RestController
class FacebookCredentialController {

    @NonNull
    private final FacebookCredentialService facebookCredentialService;

    /**
     * @param provider {@link Credential#getProvider()}
     * @param label    {@link Credential#getLabel()}
     */
    @Operation(summary = "Register credential from Facebook account",
            description = """
                    Use the "Login with Facebook" process.<br>
                    Based on OAuth2 "authorization" flow.<br>
                    *No Facebook credential is registered (or logged, ...)!*
                    """)
    @PostMapping(value = "/credential/facebook", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(CREATED)
    public Mono<RegisteredCredentialDto> register(
            @RequestPart @Parameter(/*FIXME*/ required = true) Provider provider,
            @RequestPart @Parameter(/*FIXME*/ required = true, description = "Facebook email") String email,
            @RequestPart @Parameter(/*FIXME*/ required = true, description = "Facebook password") String password,
            @RequestPart @Parameter(/*FIXME*/ required = true, description = "UI information") String label) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> facebookCredentialService.register(userId, provider, email, password, label))
                .onErrorMap(FacebookAccountLockedException.class, e ->
                        new ResponseStatusException(LOCKED, "Facebook locked your account, because the server IP is not (yet) authorized... Please login to your Facebook account, and authorize the server IP!"));
    }

}
