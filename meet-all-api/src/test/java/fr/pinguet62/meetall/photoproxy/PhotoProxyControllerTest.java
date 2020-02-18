package fr.pinguet62.meetall.photoproxy;

import fr.pinguet62.meetall.security.utils.DisableWebFluxSecurity;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URL;

import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RunWith(SpringRunner.class)
@WebFluxTest(PhotoProxyController.class)
@AutoConfigureWebClient
@DisableWebFluxSecurity
public class PhotoProxyControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private MockWebServer server;

    @Before
    public void startServer() {
        server = new MockWebServer();
    }

    @After
    public void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    public void proxifyPhoto_returnsResponseHeaders() {
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
