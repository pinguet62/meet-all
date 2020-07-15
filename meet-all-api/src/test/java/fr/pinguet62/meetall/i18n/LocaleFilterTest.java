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
import static fr.pinguet62.meetall.i18n.LocaleUtils.getSupportedLocales;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
    public void supportedLocale_useIt() {
        webTestClient.get()
                .uri("/i18n")
                .header(ACCEPT_LANGUAGE, "fr")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("fr");
    }

    @Test
    public void listWithQualityValues_useMoreSpecific() {
        webTestClient.get()
                .uri("/i18n")
                .header(ACCEPT_LANGUAGE, "zh, fr;q=0.9, en;q=0.8, de;q=0.7, *;q=0.5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("fr");
    }

    @Test
    public void listWithQualityValues_useDefaultWhenNoSupported() {
        webTestClient.get()
                .uri("/i18n")
                .header(ACCEPT_LANGUAGE, "zh, it;q=0.9, es;q=0.8, *;q=0.5")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("en");
    }

    @Test
    public void unsupportedLocale_useDefault() {
        final Locale locale = new Locale("es");
        assertThat(locale, is(not(in(getSupportedLocales()))));

        webTestClient.get()
                .uri("/i18n")
                .header(ACCEPT_LANGUAGE, "es")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("en");
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
