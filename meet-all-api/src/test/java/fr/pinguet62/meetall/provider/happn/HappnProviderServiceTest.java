package fr.pinguet62.meetall.provider.happn;

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
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.provider.happn.HappnProviderService.HEADER;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class HappnProviderServiceTest {

    private final String authToken = "authToken";

    private MockWebServer server;
    private HappnProviderService happnProvider;

    @Before
    public void startServer() {
        server = new MockWebServer();
        happnProvider = new HappnProviderService(server.url("/").toString());
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

        ProfileDto profile = happnProvider.getProfile(authToken, userId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("users/" + userId)))),
                header(HEADER, "OAuth=\"" + authToken + "\""))));
        assertThat(profile, is(new ProfileDto(
                "48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1",
                "Sophie",
                26,
                asList(
                        "https://1675564c27.optimicdn.com/cache/images/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/320-320.0_559a0790-d18a-11e8-9e28-196ec4dfc48e.jpg",
                        "https://1675564c27.optimicdn.com/cache/images/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/320-320.0_3219f290-c117-11e8-b57d-0fc094ce31f9.jpg"))));
    }

    @Test
    public void getConversations() throws Exception {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("matches.json")));

        List<ConversationDto> conversations = happnProvider.getConversations(authToken).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("users/me/conversations")))),
                header(HEADER, "OAuth=\"" + authToken + "\""))));
        assertThat(conversations.get(1), is(new ConversationDto(
                "6758b9e9-7691-4acb-8096-9a8c596098dc_93833b7c-a427-45be-b3cd-81d068108184",
                new ProfileDto(
                        "93833b7c-a427-45be-b3cd-81d068108184",
                        "Cha",
                        30,
                        asList(
                                "https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7b745d40-aed9-11e8-9d0d-0f272e08b1ea.jpg",
                                "https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7c213f10-aed9-11e8-a9a7-0d1f7e69a1ae.jpg"
                        )),
                OffsetDateTime.parse("2018-11-25T09:47:58+00:00").toZonedDateTime(),
                new MessageDto(
                        "1543139278_30e2ad90-f097-11e8-b59a-bf3837b6acfb",
                        OffsetDateTime.parse("2018-11-25T09:47:58+00:00").toZonedDateTime(),
                        false,
                        "Bonjour, comment vas tu ? :)"
                )
        )));
    }

    @Test
    public void getMessages() throws Exception {
        final String matchId = "matchId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readFile("messages.json")));

        List<MessageDto> messages = happnProvider.getMessages(authToken, matchId).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("conversations/" + matchId + "/messages")))),
                header(HEADER, "OAuth=\"" + authToken + "\""))));
        assertThat(messages, hasSize(20));
        assertThat(messages.get(0), is(new MessageDto(
                "1542396170_021ed100-e9d5-11e8-97b3-21468353071b",
                OffsetDateTime.parse("2018-11-16T19:22:50+00:00").toZonedDateTime(),
                false,
                "Salut, ça va et toi ? Pour être sincère avec toi, j’ai passé une bonne soirée, on a bien papoté mais je n’ai pas eu le petit truc...")));
        assertThat(messages.get(1), is(new MessageDto(
                "1542395520_7e855a40-e9d3-11e8-97b3-21468353071b",
                OffsetDateTime.parse("2018-11-16T19:12:00+00:00").toZonedDateTime(),
                true,
                "Ah compris : fameux tiers des personnes pas très sincères qui n'envoient plus de message.")));
    }

    private String readFile(String resource) throws IOException, URISyntaxException {
        Path path = Paths.get(getClass().getResource(resource).toURI());
        try (Stream<String> lines = Files.lines(path)) {
            return lines.collect(joining(lineSeparator()));
        }
    }

}
