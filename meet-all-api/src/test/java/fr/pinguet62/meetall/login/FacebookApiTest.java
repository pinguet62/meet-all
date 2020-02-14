package fr.pinguet62.meetall.login;

import fr.pinguet62.meetall.login.FacebookApi.MeResponseDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;

import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class FacebookApiTest {

    private MockWebServer server;

    private FacebookApi facebookApi;

    @Before
    public void startServer() {
        server = new MockWebServer();
        facebookApi = new FacebookApi(WebClient.builder(), server.url("/").toString());
    }

    @After
    public void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    public void getMe() {
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
    public void getMe_invalidToken() {
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
