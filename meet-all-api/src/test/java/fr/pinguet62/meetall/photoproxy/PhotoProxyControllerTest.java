package fr.pinguet62.meetall.photoproxy;

import fr.pinguet62.meetall.security.utils.DisableWebFluxSecurity;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URL;

import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.ACCEPTED;

@WebFluxTest(PhotoProxyController.class)
@AutoConfigureWebClient
@DisableWebFluxSecurity
class PhotoProxyControllerTest {

    @Autowired
    WebTestClient webTestClient;

    MockWebServer server;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void returnsResponseHeaders() {
        final String originalUrl = server.url("/").toString() + "static/images/logo.svg";
        final HttpStatus status = ACCEPTED;
        final String body = "anything";

        server.enqueue(new MockResponse()
                .setResponseCode(status.value())
                .setHeader("Content-Type", "image/png") // content-type
                .setHeader("Cache-Control", "public, max-age=14400") // cache
                .setHeader("Server", "cloudflare") // custom
                .setBody(body));

        String proxifiedUrl = PhotoProxyEncoder.encode(originalUrl);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/photo").pathSegment(proxifiedUrl).build())
                .exchange()
                .expectStatus().isEqualTo(status)
                .expectHeader().valueEquals("Content-Type", "image/png")
                .expectHeader().valueEquals("Cache-Control", "public, max-age=14400")
                .expectHeader().valueEquals("Server", "cloudflare")
                .expectBody(String.class).isEqualTo(body);

        assertThat(server, takingRequest(
                url(with(HttpUrl::url, with(URL::toString, is(originalUrl))))));
    }

}
