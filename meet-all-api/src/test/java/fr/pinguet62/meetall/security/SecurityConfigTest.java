package fr.pinguet62.meetall.security;

import fr.pinguet62.meetall.config.OpenApiConfig;
import lombok.SneakyThrows;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import static fr.pinguet62.meetall.security.JwtTokenGenerator.ALGORITHM;
import static fr.pinguet62.meetall.security.SecurityConfigTest.TestController;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SecurityConfig.class, OpenApiConfig.class, TestController.class, JwtTokenGenerator.class},
        properties = {
                // "spring.security.oauth2.resourceserver.jwt.key-value = ",
        })
@AutoConfigureWebTestClient
public class SecurityConfigTest {

    private static SecretKey KEY;

    @BeforeClass
    public static void initKey() {
        KEY = generateRandomSecretKey();
        System.setProperty("spring.security.oauth2.resourceserver.jwt.key-value", SecretKeyUtils.toString(KEY));
    }

    @SneakyThrows
    private static SecretKey generateRandomSecretKey() {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    @EnableWebFlux
    @RestController
    public static class TestController {
        @GetMapping("/userId")
        public Mono<String> showUserId() {
            return ApplicationReactiveSecurityContextHolder.getAuthentication()
                    .map(ApplicationAuthentication::getUserId);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Test
    public void noToken() {
        final String jwtToken = null;

        webTestClient.get().uri("/userId")
                //.header(AUTHORIZATION, null)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void badJwtToken() {
        final String jwtToken = "bad";

        webTestClient.get().uri("/userId")
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().value(WWW_AUTHENTICATE, containsString("invalid_token"));
    }

    @Ignore("JWS support only 1 algorithm (HS256)")
    @Test
    public void badJwtAlgorithm() {
    }

    @Test
    public void badJwtSignature() {
        final String userId = "3";

        String jwtToken = jwtTokenGenerator.generateToken(userId, ALGORITHM, generateRandomSecretKey());
        webTestClient.get().uri("/userId")
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().value(WWW_AUTHENTICATE, containsString("invalid_token"));
    }

    @Test
    public void ok() {
        final String userId = "3";

        String jwtToken = jwtTokenGenerator.generateToken(userId);
        webTestClient.get().uri("/userId")
                .header(AUTHORIZATION, "Bearer " + jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(userId);
    }

    /**
     * {@code is(not(UNAUTHORIZED))} because {@code 404} because module not loaded
     */
    @Test
    public void openApi() {
        webTestClient.get()
                .uri("/swagger-ui.html")
                .exchange()
                .expectStatus().value(is(not(UNAUTHORIZED.value())));
        webTestClient.get()
                .uri("/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config")
                .exchange()
                .expectStatus().value(is(not(UNAUTHORIZED.value())));
        webTestClient.get()
                .uri("/v3/api-docs/swagger-config")
                .exchange()
                .expectStatus().value(is(not(UNAUTHORIZED.value())));
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().value(is(not(UNAUTHORIZED.value())));
    }
}
