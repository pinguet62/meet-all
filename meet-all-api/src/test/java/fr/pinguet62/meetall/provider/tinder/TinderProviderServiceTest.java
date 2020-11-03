package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.ExpiredTokenException;
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
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static fr.pinguet62.meetall.MatcherUtils.header;
import static fr.pinguet62.meetall.MatcherUtils.takingRequest;
import static fr.pinguet62.meetall.MatcherUtils.throwing;
import static fr.pinguet62.meetall.MatcherUtils.url;
import static fr.pinguet62.meetall.MatcherUtils.with;
import static fr.pinguet62.meetall.TestUtils.readResource;
import static fr.pinguet62.meetall.provider.tinder.TinderClient.HEADER;
import static java.time.ZoneOffset.ofHoursMinutes;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class TinderProviderServiceTest {

    final String authToken = "authToken";

    MockWebServer server;
    final Clock clock = Clock.fixed(
            OffsetDateTime.of(2020, 7, 10, 21, 52, 43, 0, ofHoursMinutes(1, 30)).toInstant(),
            ofHoursMinutes(1, 30));
    TinderProviderService tinderProvider;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        tinderProvider = new TinderProviderService(clock, new TinderClient(WebClient.builder(), server.url("/").toString()));
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void login() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/auth_login_facebook.json")));

        String authToken = tinderProvider.loginWithFacebook("EAAGm0PX4Z").block();

        assertThat(authToken, is("7689631f-c20b-406d-925c-ff0c89fdca3d"));
    }

    @Nested
    class getProposals {
        @Test
        void ok() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/profile.json")));
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/recs_core.json")));

            List<ProposalDto> proposals = tinderProvider.getProposals(authToken).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("profile")))),
                    header(HEADER, authToken))));
            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("recs/core")))),
                    header(HEADER, authToken))));
            assertThat(proposals, contains(
                    new ProposalDto(
                            "5c309d64cbb73e636034dc15",
                            new ProfileDto(
                                    "5c309d64cbb73e636034dc15",
                                    "Laetitia",
                                    29,
                                    null,
                                    List.of(
                                            "https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_3cfd9990-392c-42c4-8c70-dae8fa620930.jpg",
                                            "https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_496f518b-a369-4740-8d95-f19bdb2a3572.jpg"))),
                    new ProposalDto(
                            "5c30dbfb7f49061862de6256",
                            new ProfileDto(
                                    "5c30dbfb7f49061862de6256",
                                    "Florine",
                                    29,
                                    "Steuplé, Fais moi rire !",
                                    List.of(
                                            "https://images-ssl.gotinder.com/5c30dbfb7f49061862de6256/1080x1080_8b718deb-9c11-4835-86e7-100c613f865e.jpg")))));
        }

        @Test
        void tokenExpired() {
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/profile_tokenExpired401.json")));

            assertThat(() -> tinderProvider.getProposals(authToken).collectList().block(), throwing(ExpiredTokenException.class));
        }
    }

    @Nested
    class passProposal {
        @Test
        void ok() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/pass.json")));

            Void matched = tinderProvider.passProposal(authToken, userId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("pass/" + userId)))),
                    header(HEADER, authToken))));
            assertThat(matched, nullValue());
        }

        @Test
        void tokenExpired() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/pass_tokenExpired401.json")));

            assertThat(() -> tinderProvider.passProposal(authToken, userId).block(), throwing(ExpiredTokenException.class));
        }
    }

    @Nested
    class likeProposal {
        @Test
        void notMatched() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_not-matched.json")));

            Boolean matched = tinderProvider.likeProposal(authToken, userId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                    header(HEADER, authToken))));
            assertThat(matched, allOf(
                    notNullValue(),
                    is(false)));
        }

        @Test
        void matched() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_matched.json")));

            Boolean matched = tinderProvider.likeProposal(authToken, userId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                    header(HEADER, authToken))));
            assertThat(matched, allOf(
                    notNullValue(),
                    is(true)));
        }

        @Test
        void tokenExpired() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_tokenExpired401.json")));

            assertThat(() -> tinderProvider.likeProposal(authToken, userId).block(), throwing(ExpiredTokenException.class));
        }

        @Test
        void likeRemaining() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_like-remaining.json")));

            assertThat(() -> tinderProvider.likeProposal(authToken, userId).block(), throwing(RuntimeException.class));
            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                    header(HEADER, authToken))));
        }
    }

    @Nested
    class getConversations {
        @Test
        void ok() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta.json")));
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches.json")));

            List<ConversationDto> conversations = tinderProvider.getConversations(authToken).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("meta")))),
                    header(HEADER, authToken))));
            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("v2/matches")))),
                    header(HEADER, authToken))));
            assertThat(conversations, contains(
                    new ConversationDto(
                            "52b4d9ed6c5685412c0002a15bcc1a0cf51fe1f73b780558",
                            new LazyProfileDto(
                                    "5bcc1a0cf51fe1f73b780558",
                                    "Marie",
                                    "https://images-ssl.gotinder.com/5bcc1a0cf51fe1f73b780558/1080x1080_3ea65ee2-33e1-4a8d-8860-73a7cf2a0a7c.jpg"),
                            ZonedDateTime.of(2018, 10, 29, 14, 44, 50, 422 * 1000000, ZoneId.of("UTC")),
                            null),
                    new ConversationDto(
                            "52b4d9ed6c5685412c0002a15aafa40957398e766afe7ed4",
                            new LazyProfileDto(
                                    "5aafa40957398e766afe7ed4",
                                    "Alexandra",
                                    "https://images-ssl.gotinder.com/5aafa40957398e766afe7ed4/1080x1080_e13be39b-a619-427b-8359-7337e04250f3.jpg"),
                            ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500 * 1000000, ZoneId.of("UTC")),
                            new LazyMessageDto(
                                    ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500 * 1000000, ZoneId.of("UTC")),
                                    true,
                                    "Alors les entretiens RH sur Tinder, ça embuache ? 😋"))));
        }

        @Test
        void tokenExpired() {
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta_tokenExpired401.json")));

            assertThat(() -> tinderProvider.getConversations(authToken).collectList().block(), throwing(ExpiredTokenException.class));
        }
    }

    @Nested
    class getMessages {
        @Test
        void ok() {
            final String matchId = "matchId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta.json")));
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_messages.json")));

            List<MessageDto> messages = tinderProvider.getMessages(authToken, matchId).collectList().block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("meta")))),
                    header(HEADER, authToken))));
            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("v2/matches/" + matchId + "/messages")))),
                    header(HEADER, authToken))));
            assertThat(messages, contains(
                    new MessageDto(
                            "5bd2e800e46c4dd64d1a0866",
                            ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500 * 1000000, ZoneId.of("UTC")),
                            true,
                            "Alors les entretiens RH sur Tinder, ça embuache ? 😋"),
                    new MessageDto(
                            "5bd2e7e67cd91a673b1a9873",
                            ZonedDateTime.of(2018, 10, 26, 10, 9, 42, 830 * 1000000, ZoneId.of("UTC")),
                            false,
                            "Bonjour Julien")));
        }

        @Test
        void tokenExpired() {
            final String matchId = "matchId";
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta_tokenExpired401.json")));

            assertThat(() -> tinderProvider.getMessages(authToken, matchId).collectList().block(), throwing(ExpiredTokenException.class));
        }
    }

    @Nested
    class sendMessage {
        @Test
        void ok() {
            final String matchId = "5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_sendMessage.json")));

            MessageDto message = tinderProvider.sendMessage(authToken, matchId, "text").block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("user/matches/" + matchId)))),
                    header(HEADER, authToken))));
            assertThat(message, is(new MessageDto(
                    "5c157885c5ee3f110007b6fc",
                    ZonedDateTime.of(2018, 12, 15, 21, 56, 21, 322 * 1000000, ZoneId.of("UTC")),
                    true,
                    "Et perso je fonctionne beaucoup au feeling")));
        }

        @Test
        void tokenExpired() {
            final String matchId = "5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530";
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_sendMessage_tokenExpired401.json")));

            assertThat(() -> tinderProvider.sendMessage(authToken, matchId, "text").block(), throwing(ExpiredTokenException.class));
        }
    }

    @Nested
    class getProfile {
        @Test
        void ok() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user.json")));

            ProfileDto profile = tinderProvider.getProfile(authToken, userId).block();

            assertThat(server, takingRequest(allOf(
                    url(with(HttpUrl::url, with(URL::toString, containsString("user/" + userId)))),
                    header(HEADER, authToken))));
            assertThat(profile, is(new ProfileDto(
                    "5b486956f408df634d26de3b",
                    "Al",
                    31,
                    "My description",
                    List.of(
                            "https://images-ssl.gotinder.com/5b486956f408df634d26de3b/1080x1080_7cd50312-0063-4814-89b0-1568886056ba.jpg"))));
        }

        @Test
        void tokenExpired() {
            final String userId = "userId";
            server.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user_tokenExpired401.json")));

            assertThat(() -> tinderProvider.getProfile(authToken, userId).block(), throwing(ExpiredTokenException.class));
        }
    }
}
