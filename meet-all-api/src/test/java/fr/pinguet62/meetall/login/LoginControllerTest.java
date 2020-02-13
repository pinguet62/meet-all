package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.security.utils.DisableWebFluxSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(LoginController.class)
@DisableWebFluxSecurity
        // public routes
class LoginControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    LoginService loginService;

    /**
     * @see LoginController#login(String)
     */
    @Test
    @WithAnonymousUser
    void login() {
        final String facebookToken = "accessToken";
        final String jwtToken = "jwtToken";

        when(loginService.login(facebookToken)).thenReturn(Mono.just(jwtToken));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/login")
                        .queryParam("facebook_token", facebookToken)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(jwtToken);
    }
}
