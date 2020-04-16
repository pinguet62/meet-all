package fr.pinguet62.meetall.facebookcredential;

import fr.pinguet62.meetall.credential.RegisteredCredentialDto;
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
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.Utils.mapOf;
import static fr.pinguet62.meetall.facebookcredential.FacebookCredentialControllerTest.currentUserId;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartData;

@RunWith(SpringRunner.class)
@WebFluxTest(FacebookCredentialController.class)
@WithMockUserId(currentUserId)
public class FacebookCredentialControllerTest {

    /**
     * @see WithMockUserId
     */
    static final String currentUserId = "currentUserId";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FacebookCredentialService facebookCredentialService;

    /**
     * @see FacebookCredentialController#register(Provider, String, String, String)
     */
    @Test
    public void register() {
        final String credentialId = "99";
        final Provider provider = TINDER;
        final String email = "example@test.org";
        final String password = "aZeRtY";
        final String label = "Sample";
        final boolean ok = true;

        when(facebookCredentialService.register(currentUserId, TINDER, email, password, label))
                .thenReturn(Mono.just(new RegisteredCredentialDto(credentialId, label, provider, true)));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/credential/facebook")
                .body(fromMultipartData(new LinkedMultiValueMap<>(mapOf(
                        "provider", List.of(TINDER),
                        "email", List.of(email),
                        "password", List.of(password),
                        "label", List.of(label)))))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(credentialId)
                .jsonPath("$.label").isEqualTo(label)
                .jsonPath("$.provider").isEqualTo(provider.name())
                .jsonPath("$.ok").isEqualTo(ok);
    }
}
