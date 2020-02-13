package fr.pinguet62.meetall.facebookcredential;

import fr.pinguet62.meetall.credential.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.utils.WithMockUserId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fr.pinguet62.meetall.facebookcredential.FacebookCredentialControllerTest.currentUserId;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.LOCKED;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@WebFluxTest(FacebookCredentialController.class)
@WithMockUserId(currentUserId)
class FacebookCredentialControllerTest {

    /**
     * @see WithMockUserId
     */
    static final String currentUserId = "currentUserId";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    FacebookCredentialService facebookCredentialService;

    @Nested
    @WithMockUserId(currentUserId)
    class register {
        /**
         * @see FacebookCredentialController#register(Locale, Provider, String, String, String)
         */
        @Test
        void success() {
            final String credentialId = "99";
            final Provider provider = TINDER;
            final String email = "example@test.org";
            final String password = "aZeRtY";
            final String label = "Sample";
            final boolean ok = true;

            when(facebookCredentialService.register(currentUserId, TINDER, email, password, label))
                    .thenReturn(Mono.just(new RegisteredCredentialDto(credentialId, label, provider, true)));

            webTestClient.mutateWith(csrf())
                    .post()
                    .uri("/credential/facebook")
                    .body(fromMultipartData(new LinkedMultiValueMap<>(Map.of(
                            "provider", List.of(TINDER),
                            "email", List.of(email),
                            "password", List.of(password),
                            "label", List.of(label)))))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(credentialId)
                    .jsonPath("$.label").isEqualTo(label)
                    .jsonPath("$.provider").isEqualTo(provider.name())
                    .jsonPath("$.ok").isEqualTo(ok);
        }

        /**
         * @see FacebookCredentialController#register(Locale, Provider, String, String, String)
         */
        @Test
        void InvalidCredentialsException() {
            String message = "L’e-mail entré ne correspond à aucun compte.";
            when(facebookCredentialService.register(any(), any(), any(), any(), any()))
                    .thenReturn(Mono.error(new InvalidCredentialsException(message)));

            webTestClient.mutateWith(csrf())
                    .post()
                    .uri("/credential/facebook")
                    .header(ACCEPT_LANGUAGE, "fr")
                    .body(fromMultipartData(new LinkedMultiValueMap<>(Map.of(
                            "provider", List.of(TINDER),
                            "email", List.of("unknown@test.org"),
                            "password", List.of("bad"),
                            "label", List.of("Sample")))))
                    .exchange()
                    .expectStatus().isEqualTo(BAD_REQUEST)
                    .expectBody().jsonPath("$.message").value(containsString(message));
        }

        /**
         * @see FacebookCredentialController#register(Locale, Provider, String, String, String)
         */
        @Test
        void FacebookAccountLockedException() {
            when(facebookCredentialService.register(any(), any(), any(), any(), any()))
                    .thenReturn(Mono.error(new FacebookAccountLockedException()));

            webTestClient.mutateWith(csrf())
                    .post()
                    .uri("/credential/facebook")
                    .header(ACCEPT_LANGUAGE, "fr")
                    .body(fromMultipartData(new LinkedMultiValueMap<>(Map.of(
                            "provider", List.of(TINDER),
                            "email", List.of("example@test.org"),
                            "password", List.of("aZeRtY"),
                            "label", List.of("Sample")))))
                    .exchange()
                    .expectStatus().isEqualTo(LOCKED)
                    .expectBody().jsonPath("$.message").value(containsString("Facebook a bloqué"));
        }
    }
}
