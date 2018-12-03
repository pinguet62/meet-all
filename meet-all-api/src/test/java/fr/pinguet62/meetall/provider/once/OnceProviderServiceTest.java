package fr.pinguet62.meetall.provider.once;

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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.provider.once.OnceProviderService.HEADER;
import static java.lang.System.lineSeparator;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class OnceProviderServiceTest {

    private final String authToken = "authToken";

    private MockWebServer server;
    private OnceProviderService onceProvider;

    @Before
    public void startServer() {
        server = new MockWebServer();
        onceProvider = new OnceProviderService(server.url("/").toString());
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
                .setBody(readFile("match.json")));

        ProfileDto profile = onceProvider.getProfile(authToken, userId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("match/" + userId)))),
                header(HEADER, authToken))));
        assertThat(profile, is(new ProfileDto(
                "MEA346007886",
                "Louise",
                27,
                asList(
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg",
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485380_original.jpg"))));
    }

    @Test
    public void getConversations() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("connections.json")));

        List<ConversationDto> conversations = onceProvider.getConversations(authToken).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("v1/connections")))),
                header(HEADER, authToken))));
        assertThat(conversations, contains(
                new ConversationDto(
                        "MEA346007886",
                        new ProfileDto(
                                "MEA346007886",
                                "Louise",
                                27,
                                singletonList("https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg")),
                        ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                        new MessageDto(
                                "MEA346007886::2",
                                ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                                true,
                                "Bonsoir la timide :p"))));
    }

    @Test
    public void getMessages() throws Exception {
        final String matchId = "matchId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("messages.json")));

        List<MessageDto> messages = onceProvider.getMessages(authToken, matchId).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("messages?match_id=" + matchId)))),
                header(HEADER, authToken))));
        assertThat(messages, hasSize(2));
        assertThat(messages.get(0), is(new MessageDto(
                "MEA346007886::2",
                ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                true,
                "Bonsoir la timide :p")));
        assertThat(messages.get(1), is(new MessageDto(
                "MEA346007886::1",
                ZonedDateTime.of(2018, 11, 24, 13, 36, 50, 0, UTC),
                true,
                "Vous avez été connectés")));
    }

    private String readFile(String resource) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resource).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(joining(lineSeparator()));
        }
    }

}
