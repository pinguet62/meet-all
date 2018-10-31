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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .queryParam("email", email)
                        .queryParam("password", password)
                        .build())
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
                .uri(uriBuilder -> uriBuilder.path("/login")
                        .queryParam("email", email)
                        .queryParam("password", password)
                        .build())
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
     * @see LoginController#registerCredentials(Provider, String, String)
     */
    @Test
    public void registerCredentials() {
        final int currentUserId = 42;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.registerCredentials(currentUserId, provider, credential, label)).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/credential")
                        .queryParam("provider", provider)
                        .queryParam("credential", credential)
                        .queryParam("label", label)
                        .build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists() // generated
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

    /**
     * @see LoginController#updateCredentials(int, String, String)
     */
    @Test
    public void updateCredentials() {
        final int currentUserId = 42;
        final int id = 99;
        final Provider provider = TINDER;
        final String credential = "credential";
        final String label = "label";

        when(loginService.updateCredentials(currentUserId, id, ofNullable(credential), ofNullable(label))).thenReturn(Mono.just(new ProviderCredential(99, new User(), provider, credential, label)));

        webTestClient.put()
                .uri(uriBuilder -> uriBuilder.path("/credential").pathSegment(valueOf(id))
                        .queryParam("credential", credential)
                        .queryParam("label", label)
                        .build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.credential").doesNotExist(); // secret!
    }

}
