package fr.pinguet62.meetall.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.web.access.server.BearerTokenServerAccessDeniedHandler;
import org.springframework.security.oauth2.server.resource.web.server.ServerBearerTokenAuthenticationConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.security.JwtTokenGenerator.ALGORITHM;
import static fr.pinguet62.meetall.security.SecurityConfigTest.TestController;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityConfig.class, TestController.class, JwtTokenGenerator.class},
        properties = "spring.security.oauth2.jwt.secret = test")
@AutoConfigureWebTestClient
public class SecurityConfigTest {

    @EnableWebFlux
    @RestController
    public static class TestController {
        @GetMapping("/userId")
        public Mono<String> showUserId() {
            return ApplicationReactiveSecurityContextHolder.getAuthentication()
                    .map(ApplicationAuthentication::getUserId);
        }
    }

    /**
     * @see ServerBearerTokenAuthenticationConverter
     */
    public static final String HEADER_KEY = AUTHORIZATION;
    /**
     * @see ServerBearerTokenAuthenticationConverter
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * @see BearerTokenServerAccessDeniedHandler
     */
    public static final HttpStatus ERROR_STATUS = FORBIDDEN;
    /**
     * @see BearerTokenServerAccessDeniedHandler
     */
    public static final String ERROR_HEADER = WWW_AUTHENTICATE;
    /**
     * @see BearerTokenServerAccessDeniedHandler
     */
    public static final String REALM = "Bearer";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Test
    public void noToken() {
        String jwtToken = null;
        webTestClient.get().uri("/userId")
                //.header(HEADER_KEY, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void badJwtToken() {
        String jwtToken = "bad";
        webTestClient.get().uri("/userId")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isEqualTo(ERROR_STATUS.value())
                .expectHeader().value(ERROR_HEADER, is(REALM));
    }

    /**
     * Signature algorithm: {@code HS256}
     */
    @Test
    public void badJwtAlgorithm() {
        final String userId = "3";

        String jwtToken = jwtTokenGenerator.generateToken(userId, Algorithm::HMAC512, "test");
        webTestClient.get().uri("/userId")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isEqualTo(ERROR_STATUS.value())
                .expectHeader().value(ERROR_HEADER, is(REALM));
    }

    /**
     * Signature key: {@code "bad"}
     */
    @Test
    public void badJwtSignature() {
        final String userId = "3";

        String jwtToken = jwtTokenGenerator.generateToken(userId, ALGORITHM, "bad");
        webTestClient.get().uri("/userId")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isEqualTo(ERROR_STATUS.value())
                .expectHeader().value(ERROR_HEADER, is(REALM));
    }

    @Test
    public void ok() {
        final String userId = "3";

        String jwtToken = jwtTokenGenerator.generateToken(userId);
        webTestClient.get().uri("/userId")
                .header(HEADER_KEY, TOKEN_PREFIX + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(userId);
    }

}
