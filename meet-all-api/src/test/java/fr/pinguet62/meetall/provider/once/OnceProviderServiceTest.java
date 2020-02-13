package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.model.ConversationDto;
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
import static org.hamcrest.Matchers.empty;
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
        void notYetViewed() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match-notYetViewed.json")));

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
                            "Je cherche du sérieux...",
                            List.of(
                                    "https://d110abryny6tab.cloudfront.net/pictures/EA6728641/33787916_original.jpg",
                                    "https://d110abryny6tab.cloudfront.net/pictures/EA6728641/33803466_original.jpg")))));
        }

        @Test
        void alreadyLiked() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match-alreadyLiked.json")));

            List<ProposalDto> proposal = onceProvider.getProposals(authToken).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("match")))),
                    header(HEADER, authToken))));
            assertThat(proposal, is(empty()));
        }

        @Test
        void alreadyPassed() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/once/match-alreadyPassed.json")));

            List<ProposalDto> proposal = onceProvider.getProposals(authToken).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("match")))),
                    header(HEADER, authToken))));
            assertThat(proposal, is(empty()));
        }
    }

    @Nested
    class likeOrUnlikeProposal {
        @Nested
        class pass {
            @Test
            void pass() {
                final String matchId = "MEA356800065";
                server.enqueue(new MockResponse()
                        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_pass.json")));

                Boolean matched = onceProvider.likeOrUnlikeProposal(authToken, matchId, false).block();

                assertThat(server, takingRequest(allOf(
                        url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId + "/pass")))),
                        header(HEADER, authToken))));
                assertThat(matched, nullValue());
            }
        }

        @Nested
        class like {
            @Test
            void notMatched() {
                final String matchId = "MEA356800065";
                server.enqueue(new MockResponse()
                        .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                        .setBody(readResource("/fr/pinguet62/meetall/provider/once/match_like_not-matched.json")));

                Boolean matched = onceProvider.likeOrUnlikeProposal(authToken, matchId, true).block();

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

                Boolean matched = onceProvider.likeOrUnlikeProposal(authToken, matchId, true).block();

                assertThat(server, takingRequest(allOf(
                        url(with(HttpUrl::url, with(URL::toString, containsString("match/" + matchId + "/like")))),
                        header(HEADER, authToken))));
                assertThat(matched, allOf(
                        notNullValue(),
                        is(true)));
            }
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
                        new ProfileDto(
                                "MEA356404742",
                                "Marine",
                                27,
                                null, // not filled
                                List.of("https://d110abryny6tab.cloudfront.net/pictures/EA7640585/34002662_original.jpg")),
                        ZonedDateTime.of(2019, 1, 7, 21, 24, 26, 0, UTC),
                        null),
                new ConversationDto(
                        "MEA346007886",
                        new ProfileDto(
                                "MEA346007886",
                                "Louise",
                                27,
                                null, // not filled
                                List.of("https://d110abryny6tab.cloudfront.net/pictures/EA7464845/33485378_original.jpg")),
                        ZonedDateTime.of(2018, 11, 24, 18, 37, 48, 0, UTC),
                        new MessageDto(
                                "MEA346007886::2",
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
