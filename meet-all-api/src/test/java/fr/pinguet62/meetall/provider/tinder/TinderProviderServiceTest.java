package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.exception.ExpiredTokenException;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
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
        tinderProvider = new TinderProviderService(new TinderClient(server.url("/").toString()));
    }

    @After
    public void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    public void getProposals() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/profile.json")));
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
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
                                28,
                                asList(
                                        "https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_3cfd9990-392c-42c4-8c70-dae8fa620930.jpg",
                                        "https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_496f518b-a369-4740-8d95-f19bdb2a3572.jpg"))),
                new ProposalDto(
                        "5c30dbfb7f49061862de6256",
                        new ProfileDto(
                                "5c30dbfb7f49061862de6256",
                                "Florine",
                                28,
                                singletonList(
                                        "https://images-ssl.gotinder.com/5c30dbfb7f49061862de6256/1080x1080_8b718deb-9c11-4835-86e7-100c613f865e.jpg")))));
    }

    @Test
    public void getProposals_tokenExpired() {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/profile_tokenExpired401.json")));

        assertThat(() -> tinderProvider.getProposals(authToken).collectList().block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void likeOrUnlikeProposal_unlike() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/pass.json")));

        Boolean matched = tinderProvider.likeOrUnlikeProposal(authToken, userId, false).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("pass/" + userId)))),
                header(HEADER, authToken))));
        assertThat(matched, nullValue());
    }

    @Test
    public void likeOrUnlikeProposal_unlike_tokenExpired() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/pass_tokenExpired401.json")));

        assertThat(() -> tinderProvider.likeOrUnlikeProposal(authToken, userId, false).block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void likeOrUnlikeProposal_like_notMatched() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_not-matched.json")));

        Boolean matched = tinderProvider.likeOrUnlikeProposal(authToken, userId, true).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                header(HEADER, authToken))));
        assertThat(matched, allOf(
                notNullValue(),
                is(false)));
    }

    @Test
    public void likeOrUnlikeProposal_like_matched() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_matched.json")));

        Boolean matched = tinderProvider.likeOrUnlikeProposal(authToken, userId, true).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                header(HEADER, authToken))));
        assertThat(matched, allOf(
                notNullValue(),
                is(true)));
    }

    @Test
    public void likeOrUnlikeProposal_like_tokenExpired() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_tokenExpired401.json")));

        assertThat(() -> tinderProvider.likeOrUnlikeProposal(authToken, userId, true).block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void likeOrUnlikeProposal_like_likeRemaining() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_like-remaining.json")));

        assertThat(() -> tinderProvider.likeOrUnlikeProposal(authToken, userId, true).block(), throwing(RuntimeException.class));
        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("like/" + userId)))),
                header(HEADER, authToken))));
    }

    @Test
    public void getConversations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta.json")));
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
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
                        new ProfileDto(
                                "5bcc1a0cf51fe1f73b780558",
                                "Marie",
                                31,
                                asList(
                                        "https://images-ssl.gotinder.com/5bcc1a0cf51fe1f73b780558/1080x1080_3ea65ee2-33e1-4a8d-8860-73a7cf2a0a7c.jpg",
                                        "https://images-ssl.gotinder.com/5bcc1a0cf51fe1f73b780558/1080x1080_d699cc0f-0f82-4add-8da9-8da8df4b39ca.jpg")),
                        ZonedDateTime.of(2018, 10, 29, 14, 44, 50, 422 * 1000000, ZoneId.of("UTC")),
                        null),
                new ConversationDto(
                        "52b4d9ed6c5685412c0002a15aafa40957398e766afe7ed4",
                        new ProfileDto(
                                "5aafa40957398e766afe7ed4",
                                "Alexandra",
                                30,
                                singletonList(
                                        "https://images-ssl.gotinder.com/5aafa40957398e766afe7ed4/1080x1080_e13be39b-a619-427b-8359-7337e04250f3.jpg")),
                        ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500 * 1000000, ZoneId.of("UTC")),
                        new MessageDto(
                                "5bd2e800e46c4dd64d1a0866",
                                ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500 * 1000000, ZoneId.of("UTC")),
                                true,
                                "Alors les entretiens RH sur Tinder, ça embuache ? 😋"))));
    }

    @Test
    public void getConversations_tokenExpired() {
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta_tokenExpired401.json")));

        assertThat(() -> tinderProvider.getConversations(authToken).collectList().block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void getMessages() {
        final String matchId = "matchId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta.json")));
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
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
    public void getMessages_tokenExpired() {
        final String matchId = "matchId";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta_tokenExpired401.json")));

        assertThat(() -> tinderProvider.getMessages(authToken, matchId).collectList().block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void sendMessage() {
        final String matchId = "5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
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
    public void sendMessage_tokenExpired() {
        final String matchId = "5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_sendMessage_tokenExpired401.json")));

        assertThat(() -> tinderProvider.sendMessage(authToken, matchId, "text").block(), throwing(ExpiredTokenException.class));
    }

    @Test
    public void getProfile() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user.json")));

        ProfileDto profile = tinderProvider.getProfile(authToken, userId).block();

        assertThat(server, takingRequest(allOf(
                url(with(HttpUrl::url, with(URL::toString, containsString("user/" + userId)))),
                header(HEADER, authToken))));
        assertThat(profile, is(new ProfileDto(
                "5b486956f408df634d26de3b",
                "Al",
                30,
                singletonList(
                        "https://images-ssl.gotinder.com/5b486956f408df634d26de3b/1080x1080_7cd50312-0063-4814-89b0-1568886056ba.jpg"))));
    }

    @Test
    public void getProfile_tokenExpired() {
        final String userId = "userId";
        server.enqueue(new MockResponse()
                .setResponseCode(401)
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user_tokenExpired401.json")));

        assertThat(() -> tinderProvider.getProfile(authToken, userId).block(), throwing(ExpiredTokenException.class));
    }

}
