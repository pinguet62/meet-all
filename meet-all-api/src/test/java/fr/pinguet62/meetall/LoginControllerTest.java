package fr.pinguet62.meetall;

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
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.Utils.mapOf;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
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

}
