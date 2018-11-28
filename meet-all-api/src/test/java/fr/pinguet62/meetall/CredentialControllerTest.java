package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.security.SecurityWebFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.Utils.mapOf;
import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@RunWith(SpringRunner.class)
@WebFluxTest(CredentialController.class)
@Import(SecurityWebFilter.class)
public class CredentialControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CredentialService credentialService;

    /**
     * @see CredentialController#getRegisteredCredentials()
     */
    @Test
    public void getRegisteredCredentials() {
        final int currentUserId = 3;

        when(credentialService.getRegisteredCredentials(currentUserId)).thenReturn(Flux.just(
                new RegisteredCredentialDto(11, "first", TINDER, true),
                new RegisteredCredentialDto(22, "second", HAPPN, false)));

        webTestClient.get()
                .uri("/credential")
                .header(AUTHORIZATION, valueOf(currentUserId))
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
     * @see CredentialController#getRegisteredCredentials()
     */
    @Test
    public void getRegisteredCredentials_secured() {
        when(credentialService.getRegisteredCredentials(anyInt())).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/credential")
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * @see CredentialController#registerCredential(Provider, String, String)
     */
    @Test
    public void registerCredential() {
        final int currentUserId = 3;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.post()
                .uri("/credential")
                .header(AUTHORIZATION, valueOf(currentUserId))
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "provider", singletonList(provider),
                        "credential", singletonList(credential),
                        "label", singletonList(label)))))
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
     * @see CredentialController#registerCredential(Provider, String, String)
     */
    @Test
    public void registerCredential_secured() {
        final int currentUserId = 3;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.post()
                .uri("/credential")
                // .header(AUTHORIZATION, null)
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "provider", singletonList(provider),
                        "credential", singletonList(credential),
                        "label", singletonList(label)))))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * @see CredentialController#updateCredential(int, String, String)
     */
    @Test
    public void updateCredential() {
        final int currentUserId = 3;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "credential", singletonList(credential),
                        "label", singletonList(label)))))
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
     * @see CredentialController#updateCredential(int, String, String)
     */
    @Test
    public void updateCredential_secured() {
        final int currentUserId = 3;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * @see CredentialController#deleteCredential(int)
     */
    @Test
    public void deleteCredential() {
        final int currentUserId = 3;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .header(AUTHORIZATION, valueOf(currentUserId))
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
    public void deleteCredential_secured() {
        final int currentUserId = 3;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(credentialService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new RegisteredCredentialDto(99, label, provider, true)));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
