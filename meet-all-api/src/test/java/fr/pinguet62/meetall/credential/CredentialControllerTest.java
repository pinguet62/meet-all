package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.utils.WithMockUserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static fr.pinguet62.meetall.credential.CredentialControllerTest.currentUserId;
import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.lang.String.valueOf;
import static java.util.Optional.of;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@WebFluxTest(CredentialController.class)
@WithMockUserId(currentUserId)
class CredentialControllerTest {

    /**
     * @see WithMockUserId
     */
    static final String currentUserId = "userId";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    CredentialService credentialService;

    /**
     * @see CredentialController#getRegisteredCredentials()
     */
    @Test
    void getRegisteredCredentials() {
        when(credentialService.getRegisteredCredentials(currentUserId)).thenReturn(Flux.just(
                new RegisteredCredentialDto("11", "first", TINDER, true),
                new RegisteredCredentialDto("22", "second", HAPPN, false)));

        webTestClient.get()
                .uri("/credential")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(11)
                .jsonPath("$[0].label").isEqualTo("first")
                .jsonPath("$[0].provider").isEqualTo(TINDER.name())
                .jsonPath("$[0].ok").isEqualTo(true)
                .jsonPath("$[1].id").isEqualTo(22)
                .jsonPath("$[1].label").isEqualTo("second")
                .jsonPath("$[1].provider").isEqualTo(HAPPN.name())
                .jsonPath("$[1].ok").isEqualTo(false);
    }

    /**
     * @see CredentialController#registerCredential(Provider, String, String)
     */
    @Test
    void registerCredential() {
        final String id = "99";
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new RegisteredCredentialDto(id, label, provider, true)));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/credential")
                .body(fromMultipartData(new LinkedMultiValueMap<>(Map.of(
                        "provider", List.of(provider),
                        "credential", List.of(credential),
                        "label", List.of(label)))))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists() // generated
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.ok").isEqualTo(true)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see CredentialController#updateCredential(String, String, String)
     */
    @Test
    void updateCredential() {
        final String id = "99";
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new RegisteredCredentialDto(id, label, provider, true)));

        webTestClient.mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .contentType(MULTIPART_FORM_DATA)
                .body(fromMultipartData(new LinkedMultiValueMap<>(Map.of(
                        "credential", List.of(credential),
                        "label", List.of(label)))))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.ok").isEqualTo(true)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see CredentialController#deleteCredential(String)
     */
    @Test
    void deleteCredential() {
        final String id = "99";
        final Provider provider = TINDER;
        final String label = "label";

        when(credentialService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new RegisteredCredentialDto("99", label, provider, true)));

        webTestClient.mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.ok").isEqualTo(true)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

}
