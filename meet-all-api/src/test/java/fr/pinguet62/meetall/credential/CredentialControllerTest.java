package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.utils.WithMockUserId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.Utils.mapOf;
import static fr.pinguet62.meetall.credential.CredentialControllerTest.currentUserId;
import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.lang.String.valueOf;
import static java.util.Optional.of;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@RunWith(SpringRunner.class)
@WebFluxTest(CredentialController.class)
@WithMockUserId(currentUserId)
public class CredentialControllerTest {

    /**
     * @see WithMockUserId
     */
    static final String currentUserId = "userId";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CredentialService credentialService;

    /**
     * @see CredentialController#getRegisteredCredentials()
     */
    @Test
    public void getRegisteredCredentials() {
        when(credentialService.getRegisteredCredentials(currentUserId)).thenReturn(Flux.just(
                new RegisteredCredentialDto(11, "first", TINDER, true),
                new RegisteredCredentialDto(22, "second", HAPPN, false)));

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
    public void registerCredential() {
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/credential")
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
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
     * @see CredentialController#updateCredential(int, String, String)
     */
    @Test
    public void updateCredential() {
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .contentType(MULTIPART_FORM_DATA)
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
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
     * @see CredentialController#deleteCredential(int)
     */
    @Test
    public void deleteCredential() {
        final int id = 99;
        final Provider provider = TINDER;
        final String label = "label";

        when(credentialService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

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
