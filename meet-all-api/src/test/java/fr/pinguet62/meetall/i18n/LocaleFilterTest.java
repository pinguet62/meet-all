package fr.pinguet62.meetall.i18n;

import fr.pinguet62.meetall.security.utils.DisableWebFluxSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Locale;

import static fr.pinguet62.meetall.i18n.LocaleFilterTest.EchoLocaleController;
import static fr.pinguet62.meetall.i18n.LocaleUtils.DEFAULT_LOCALE;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;

@WebFluxTest
@ContextConfiguration(classes = {LocaleFilter.class, EchoLocaleController.class})
@DisableWebFluxSecurity
class LocaleFilterTest {

    @RestController
    static class EchoLocaleController {
        @GetMapping("/i18n")
        public Mono<String> test() {
            return ReactiveLocaleContextHolder.getLocale().map(Locale::toString);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void headerSet_useIt() {
        webTestClient.get()
                .uri("/i18n")
                .header(ACCEPT_LANGUAGE, "es")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("es");
    }

    @Test
    public void noHeader_useDefault() {
        webTestClient.get()
                .uri("/i18n")
                /* .header(ACCEPT_LANGUAGE , null) */
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo(DEFAULT_LOCALE.toString());
    }

}
