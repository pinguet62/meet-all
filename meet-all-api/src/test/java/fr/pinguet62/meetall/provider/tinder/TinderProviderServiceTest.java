package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.provider.tinder.TinderProviderService.HEADER;
import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class TinderProviderServiceTest {

    private final String authToken = "authToken";

    private MockWebServer server;
    private TinderProviderService tinderProvider;

    @Before
    public void startServer() {
        server = new MockWebServer();
        tinderProvider = new TinderProviderService(server.url("/").toString());
    }

    @After
    public void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    public void getProfile() throws Exception {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("user.json")));

        ProfileDto profile = tinderProvider.getProfile(authToken, userId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("user/" + userId)))),
                header(HEADER, authToken))));
        assertThat(profile, not(nullValue()));
    }

    @Test
    public void getConversations() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("matches.json")));

        List<ConversationDto> conversations = tinderProvider.getConversations(authToken).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("v2/matches")))),
                header(HEADER, authToken))));
        assertThat(conversations, not(nullValue()));
    }

    @Test
    public void getMessages() throws Exception {
        final String matchId = "matchId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("meta.json")));
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("messages.json")));

        List<MessageDto> messages = tinderProvider.getMessages(authToken, matchId).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("meta")))),
                header(HEADER, authToken))));
        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("v2/matches/" + matchId + "/messages")))),
                header(HEADER, authToken))));
        assertThat(messages, hasSize(2));
    }

    private String readFile(String resource) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resource).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(joining(lineSeparator()));
        }
    }

}
