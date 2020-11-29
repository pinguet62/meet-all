package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.once.dto.OnceAuthenticateFacebookResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResultDto.OnceConnectionDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchAllResponseDto.OnceMatchAllResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchByIdResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchByIdResponseDto.OnceMatchByIdResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchLikeResponseDto.OnceMatchLikeResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchPassResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMatchResultMatchDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesResponseDto.OnceMessagesResultDto;
import fr.pinguet62.meetall.provider.once.dto.OnceSendMessageResponseDto;
import fr.pinguet62.meetall.provider.once.dto.OnceUserDto;
import fr.pinguet62.meetall.provider.once.dto.OnceUserDto.OncePictureDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static fr.pinguet62.meetall.MatcherUtils.body;
import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.methodSpring;
import static fr.pinguet62.meetall.MatcherUtils.path;
import static fr.pinguet62.meetall.MatcherUtils.query;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.uri;
import static fr.pinguet62.meetall.TestUtils.readResource;
import static fr.pinguet62.meetall.provider.once.OnceClient.HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

class OnceClientTest {

    final String authToken = "abcdefghijklmnopqrstuvwxyz";
    Matcher<RecordedRequest> hasRequiredOauthHeader = header(HEADER, authToken);

    MockWebServer server;
    OnceClient onceClient;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        onceClient = new OnceClient(WebClient.builder(), server.url("/").toString());
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void get() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, TEXT_HTML_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/get.json")));
        StepVerifier.create(onceClient.get())
                .assertNext(it -> assertThat(it, is(not(emptyString()))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/"))))));
    }

    @Test
    void authenticateFacebook() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/authenticate__facebook.json")));
        StepVerifier.create(onceClient.authenticateFacebook("ABCDEFGH"))
                .expectNext(new OnceAuthenticateFacebookResponseDto("770d2f65ca662c9a9f9a8da313afbd1ed852235d1e63f7fc"))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/v2/authenticate/facebook"))),
                body(hasJsonPath("$.facebook_token", is("ABCDEFGH"))))));
    }

    @Test
    void getMatchsHistoryFiltered() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match-history-filtered.json")));
        StepVerifier.create(onceClient.getMatchsHistoryFiltered(authToken, 42))
                .expectNext(new OnceMatchAllResponseDto(
                        new OnceMatchAllResultDto(
                                List.of(
                                        new OnceMatchResultMatchDto(
                                                "MEA508445433",
                                                new OnceUserDto(
                                                        "EA7840949",
                                                        "Anna",
                                                        26,
                                                        List.of(
                                                                new OncePictureDto("34567999_original.jpg"),
                                                                new OncePictureDto("34568212_original.jpg")),
                                                        "☺Je recherche à faire connaissance et après on verra la suite ☺\n♥♥♥"),
                                                1605587639,
                                                true,
                                                false),
                                        new OnceMatchResultMatchDto(
                                                "MEA508261701",
                                                new OnceUserDto(
                                                        "EA8734340",
                                                        "Eugénie",
                                                        28,
                                                        List.of(
                                                                new OncePictureDto("37549367_original.jpg")),
                                                        null),
                                                1605500634,
                                                false,
                                                true)),
                                "https://d110abryny6tab.cloudfront.net/pictures")))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/v1/match/history/filtered")),
                        query("size", is("42")))),
                hasRequiredOauthHeader)));
    }

    @Test
    void passMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_pass.json")));
        StepVerifier.create(onceClient.passMatch(authToken, "MEA508445433"))
                .expectNext(new OnceMatchPassResponseDto())
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/v1/match/MEA508445433/pass"))),
                hasRequiredOauthHeader)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void likeMatch(boolean connected) {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "status": "ok",
                          "result": {
                            "connected": """ + connected + "," + """
                            "message_received": null
                          }
                        }
                        """));
        StepVerifier.create(onceClient.likeMatch(authToken, "MEA508445433"))
                .expectNext(new OnceMatchLikeResponseDto(
                        new OnceMatchLikeResultDto(connected)))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/v1/match/MEA508445433/like"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getConnections() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/connections.json")));
        StepVerifier.create(onceClient.getConnections(authToken))
                .expectNext(new OnceConversationsResponseDto(
                        new OnceConversationsResultDto(
                                List.of(
                                        new OnceConnectionDto(
                                                new OnceUserDto(
                                                        "EA7640585",
                                                        "Marine",
                                                        27,
                                                        List.of(
                                                                new OncePictureDto("34002662_original.jpg")),
                                                        null), // absent
                                                "EA7640585",
                                                "MEA356404742",
                                                "Vous avez été connectés",
                                                0,
                                                1546896266),
                                        new OnceConnectionDto(
                                                new OnceUserDto(
                                                        "EA7464845",
                                                        "Louise",
                                                        27,
                                                        List.of(
                                                                new OncePictureDto("33485378_original.jpg")),
                                                        null),
                                                "EA601448",
                                                "MEA346007886",
                                                "Bonsoir la timide :p",
                                                2,
                                                1543084668)),
                                "https://d110abryny6tab.cloudfront.net/pictures")))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/v1/connections"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getMessagesForMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/messages.json")));
        StepVerifier.create(onceClient.getMessagesForMatch(authToken, "MEA346007886"))
                .expectNext(new OnceMessagesResponseDto(
                        new OnceMessagesResultDto(
                                new OnceUserDto(
                                        "EA7464845",
                                        "Louise",
                                        27,
                                        List.of(
                                                new OncePictureDto("33485378_original.jpg")),
                                        null), // absent
                                List.of(
                                        new OnceMessagesDto(
                                                "MEA346007886::3",
                                                3,
                                                "EA7464845",
                                                "test response",
                                                1545049171),
                                        new OnceMessagesDto(
                                                "MEA346007886::2",
                                                2,
                                                "EA601448",
                                                "Bonsoir la timide :p",
                                                1543084668),
                                        new OnceMessagesDto(
                                                "MEA346007886::1",
                                                1,
                                                "EA601448",
                                                "Vous avez été connectés",
                                                1543066610)
                                ))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/v1/messages")),
                        query("match_id", is("MEA346007886")))),
                hasRequiredOauthHeader)));
    }

    @Test
    void sendMessageToMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/message_send.json")));
        StepVerifier.create(onceClient.sendMessageToMatch(authToken, "MEA346007886", "Hello world!"))
                .expectNext(new OnceSendMessageResponseDto(
                        new OnceMessagesDto(
                                "MEA346007886::3",
                                3,
                                "EA601448",
                                "Hello world!",
                                1545049171)))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/v1/message"))),
                hasRequiredOauthHeader,
                body(allOf(
                        hasJsonPath("match_id", is("MEA346007886")),
                        hasJsonPath("message", is("Hello world!")))))));
    }

    @Test
    void getMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_id.json")));
        StepVerifier.create(onceClient.getMatch(authToken, "MEA346007886"))
                .expectNext(new OnceMatchByIdResponseDto(
                        new OnceMatchByIdResultDto(
                                new OnceMatchResultMatchDto(
                                        "MEA346007886",
                                        new OnceUserDto(
                                                "EA7464845",
                                                "Louise",
                                                27,
                                                List.of(
                                                        new OncePictureDto("33485378_original.jpg"),
                                                        new OncePictureDto("33485380_original.jpg")),
                                                "1m80 !"),
                                        1543058234,
                                        true, // hard
                                        false), // hard
                                "https://d110abryny6tab.cloudfront.net/pictures")))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/v1/match/MEA346007886"))),
                hasRequiredOauthHeader)));
    }
}
