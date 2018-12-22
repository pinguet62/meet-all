package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.security.utils.DisableWebFluxSecurity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.login.LoginController.TOKEN_PARAM;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(LoginController.class)
@DisableWebFluxSecurity // public routes
public class LoginControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private LoginService loginService;

    /**
     * @see LoginController#login(String)
     */
    @Test
    public void login() {
        final String accessToken = "accessToken";
        final String jwtToken = "jwtToken";

        when(loginService.login(accessToken)).thenReturn(Mono.just(jwtToken));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/login")
                        .queryParam(TOKEN_PARAM, accessToken)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(jwtToken);
    }

}
