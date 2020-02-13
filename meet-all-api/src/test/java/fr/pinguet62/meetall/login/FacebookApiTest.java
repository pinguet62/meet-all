package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.login.FacebookApi.MeResponseDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;

import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class FacebookApiTest {

    MockWebServer server;

    FacebookApi facebookApi;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        facebookApi = new FacebookApi(WebClient.builder(), server.url("/").toString());
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void ok() {
        final String accessToken = "accessToken";
        final String id = "id";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody("{ \"id\": \"" + id + "\" }"));

        Mono<MeResponseDto> response = facebookApi.getMe(accessToken);

        assertThat(response.block(), allOf(
                with(MeResponseDto::getId, is(id))));
        assertThat(server, takingRequest(
                url(with(HttpUrl::url, with(URL::toString, containsString("/me?access_token=" + accessToken))))));
    }

    @Test
    void invalidToken() {
        final String accessToken = "accessToken";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setResponseCode(BAD_REQUEST.value())
                .setBody("""
                        {
                            "error": {
                                "message": "Invalid OAuth access token.",
                                "type": "OAuthException",
                                "code": 190,
                                "fbtrace_id": "FKvHS5NDFBd"
                            }
                        }
                        """));

        Mono<MeResponseDto> response = facebookApi.getMe(accessToken);

        assertThat(response::block, throwing(RuntimeException.class));
    }
}
