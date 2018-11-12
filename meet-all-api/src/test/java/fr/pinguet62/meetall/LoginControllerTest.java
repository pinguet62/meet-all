package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.User;
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

import java.util.List;

import static fr.pinguet62.meetall.Utils.mapOf;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@RunWith(SpringRunner.class)
@WebFluxTest(LoginController.class)
@Import(SecurityWebFilter.class)
public class LoginControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoginService loginService;

    /**
     * @see LoginController#createAccount(String, String)
     */
    @Test
    public void createAccount() {
        final String email = "email";
        final String password = "password";
        final String token = "token";

        when(loginService.createAccount(email, password)).thenReturn(Mono.just(token));

        webTestClient.post()
                .uri("/user")
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "email", singletonList(email),
                        "password", singletonList(password)))))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo(token);
    }

    /**
     * @see LoginController#login(String, String)
     */
    @Test
    public void login() {
        final String email = "email";
        final String password = "password";
        final String token = "token";

        when(loginService.login(email, password)).thenReturn(Mono.just(token));

        webTestClient.post()
                .uri("/login")
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "email", singletonList(email),
                        "password", singletonList(password)))))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(token);
    }

    /**
     * @see LoginController#getRegisteredCredentials()
     */
    @Test
    public void getRegisteredCredentials() {
        final int currentUserId = 42;
        final List<ProviderCredential> providerCredentials = asList(
                new ProviderCredential(11, new User(), TINDER, "tinderAuthToken1", "first"),
                new ProviderCredential(22, new User(), TINDER, "tinderAuthToken2", "second")
        );

        when(loginService.getRegisteredCredentials(currentUserId)).thenReturn(Flux.fromIterable(providerCredentials));

        webTestClient.get()
                .uri("/credential")
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(11)
                .jsonPath("$[1].id").isEqualTo(22);
    }

    /**
     * @see LoginController#getRegisteredCredentials()
     */
    @Test
    public void getRegisteredCredentials_secured() {
        when(loginService.getRegisteredCredentials(anyInt())).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/credential")
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * @see LoginController#registerCredential(Provider, String, String)
     */
    @Test
    public void registerCredential() {
        final int currentUserId = 42;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

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
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see LoginController#registerCredential(Provider, String, String)
     */
    @Test
    public void registerCredential_secured() {
        final int currentUserId = 42;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.registerCredential(currentUserId, provider, credential, label)).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

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
     * @see LoginController#updateCredential(int, String, String)
     */
    @Test
    public void updateCredential() {
        final int currentUserId = 42;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

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
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see LoginController#updateCredential(int, String, String)
     */
    @Test
    public void updateCredential_secured() {
        final int currentUserId = 42;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.updateCredential(currentUserId, id, of(credential), of(label))).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * @see LoginController#deleteCredential(int)
     */
    @Test
    public void deleteCredential() {
        final int currentUserId = 42;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see LoginController#deleteCredential(int)
     */
    @Test
    public void deleteCredential_secured() {
        final int currentUserId = 42;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.deleteCredential(currentUserId, id)).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id)).build())
                // .header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
