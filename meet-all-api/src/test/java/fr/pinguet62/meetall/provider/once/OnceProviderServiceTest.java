package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.TestUtils.readResource;
import static fr.pinguet62.meetall.provider.once.OnceProviderService.HEADER;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
    public void getProposals() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match.json")));

        List<ProposalDto> proposal = onceProvider.getProposals(authToken).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("match")))),
                header(HEADER, authToken))));
        assertThat(proposal, contains(new ProposalDto(
                "MEA353970154",
                new ProfileDto(
                        "MEA353970154",
                        "Anne-marie",
                        27,
                        asList(
                                "https://d110abryny6tab.cloudfront.net/pictures/EA6728641/33787916_original.jpg",
                                "https://d110abryny6tab.cloudfront.net/pictures/EA6728641/33803466_original.jpg")))));
    }

    @Test
    public void getConversations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/connections.json")));

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
    public void getMessages() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/messages.json")));

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

    @Test
    public void sendMessages() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/message_send.json")));

        MessageDto message = onceProvider.sendMessage(authToken, matchId, "text").block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("v1/message")))),
                header(HEADER, authToken))));
        assertThat(message, is(new MessageDto(
                "MEA346007886::3",
                ZonedDateTime.of(2018, 12, 17, 12, 19, 31, 0, UTC),
                true,
                "text")));
    }

    @Test
    public void getProfile() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_id.json")));

        ProfileDto profile = onceProvider.getProfile(authToken, matchId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId)))),
                header(HEADER, authToken))));
        assertThat(profile, is(new ProfileDto(
                "MEA346007886",
                "Louise",
                27,
                asList(
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg",
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485380_original.jpg"))));
    }

}
