package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyMessageDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyProfileDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.TestUtils.readResource;
import static fr.pinguet62.meetall.provider.once.OnceClient.HEADER;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class OnceProviderServiceTest {

    final String authToken = "authToken";

    MockWebServer server;
    OnceProviderService onceProvider;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        onceProvider = new OnceProviderService(new OnceClient(WebClient.builder(), server.url("/").toString()));
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Nested
    class getProposals {
        @Test
        void neitherLikedNotPassed() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match-history-filtered.json")));

            List<ProposalDto> proposal = onceProvider.getProposals(authToken).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::encodedPath, containsString("match/history/filtered"))),
                    header(HEADER, authToken))));
            assertThat(proposal, contains(
                    new ProposalDto(
                            "MEA507894692",
                            new ProfileDto(
                                    "MEA507894692",
                                    "Cam",
                                    28,
                                    null,
                                    List.of(
                                            "https://d110abryny6tab.cloudfront.net/pictures/EA2305753/12968361_original.jpg"))),
                    new ProposalDto(
                            "MEA508077237",
                            new ProfileDto(
                                    "MEA508077237",
                                    "Mathilde",
                                    29,
                                    "Naturelle, souriante, sportive,curieuse.. Je recherche une personne qui a envie de se poser. ",
                                    List.of(
                                            "https://d110abryny6tab.cloudfront.net/pictures/EA6290730/29958367_original.jpg",
                                            "https://d110abryny6tab.cloudfront.net/pictures/EA6290730/29962886_original.jpg")))));
        }
    }

    @Nested
    class passProposal {
        @Test
        void pass() {
            final String matchId = "MEA356800065";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_pass.json")));

            Void matched = onceProvider.passProposal(authToken, matchId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId + "/pass")))),
                    header(HEADER, authToken))));
            assertThat(matched, nullValue());
        }
    }

    @Nested
    class likeProposal {
        @Test
        void notMatched() {
            final String matchId = "MEA356800065";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_like_not-matched.json")));

            Boolean matched = onceProvider.likeProposal(authToken, matchId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId + "/like")))),
                    header(HEADER, authToken))));
            assertThat(matched, allOf(
                    notNullValue(),
                    is(false)));
        }

        @Test
        void matched() {
            final String matchId = "MEA356800065";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_like_matched.json")));

            Boolean matched = onceProvider.likeProposal(authToken, matchId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId + "/like")))),
                    header(HEADER, authToken))));
            assertThat(matched, allOf(
                    notNullValue(),
                    is(true)));
        }
    }

    @Test
    void getConversations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/connections.json")));

        List<ConversationDto> conversations = onceProvider.getConversations(authToken).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("v1/connections")))),
                header(HEADER, authToken))));
        assertThat(conversations, contains(
                new ConversationDto(
                        "MEA356404742",
                        new LazyProfileDto(
                                "MEA356404742",
                                "Marine",
                                "https://d110abryny6tab.cloudfront.net/pictures/EA7640585/34002662_original.jpg"),
                        ZonedDateTime.of(2019, 1, 7, 21, 24, 26, 0, UTC),
                        null),
                new ConversationDto(
                        "MEA346007886",
                        new LazyProfileDto(
                                "MEA346007886",
                                "Louise",
                                "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg"),
                        ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                        new LazyMessageDto(
                                ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                                true,
                                "Bonsoir la timide :p"))));
    }

    @Test
    void getMessages() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/messages.json")));

        List<MessageDto> messages = onceProvider.getMessages(authToken, matchId).collectList().block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("messages?match_id=" + matchId)))),
                header(HEADER, authToken))));
        assertThat(messages, contains(
                new MessageDto(
                        "MEA346007886::3",
                        ZonedDateTime.of(2018, 12, 17, 12, 19, 31, 0, UTC),
                        false,
                        "test response"),
                new MessageDto(
                        "MEA346007886::2",
                        ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                        true,
                        "Bonsoir la timide :p"))
                /* filtered: first message */);
    }

    @Test
    void sendMessages() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
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
    void getProfile() {
        final String matchId = "MEA346007886";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_id.json")));

        ProfileDto profile = onceProvider.getProfile(authToken, matchId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId)))),
                header(HEADER, authToken))));
        assertThat(profile, is(new ProfileDto(
                "MEA346007886",
                "Louise",
                27,
                "1m80 !",
                List.of(
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg",
                        "https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485380_original.jpg"))));
    }
}
