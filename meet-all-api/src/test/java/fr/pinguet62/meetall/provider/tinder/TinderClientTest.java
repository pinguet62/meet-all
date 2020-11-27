package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderAuthLoginFacebookResponseDto.TinderAuthLoginFacebookResponseDataDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto.TinderMatchDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMessagesResponseDto.TinderGetMessagesDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetMetaResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderGetRecommendationsDataResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderRecommendationDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetUserResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderPassResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderPingResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto.TinderProfileDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderProfileResponseDto.TinderProfileDto.TinderProfileLikesDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto.TinderPhotoDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import static fr.pinguet62.meetall.provider.tinder.TinderClient.HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class TinderClientTest {

    final String authToken = "aaa-bb-cc-dd-eee";
    Matcher<RecordedRequest> hasRequiredOauthHeader = header(HEADER, authToken);

    MockWebServer server;
    TinderClient tinderClient;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        tinderClient = new TinderClient(WebClient.builder(), server.url("/").toString());
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void getGiphyTrending() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/giphy_trending.json")));
        StepVerifier.create(tinderClient.getGiphyTrending())
                .assertNext(it -> assertThat(it.getData(), hasSize(25)))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/giphy/trending"))))));
    }

    @Test
    void authLoginFacebook() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/auth_login_facebook.json")));
        StepVerifier.create(tinderClient.authLoginFacebook("ABCDEFGH"))
                .expectNext(new TinderAuthLoginFacebookResponseDto(
                        new TinderAuthLoginFacebookResponseDataDto("7689631f-c20b-406d-925c-ff0c89fdca3d")))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/v2/auth/login/facebook"))),
                body(hasJsonPath("$.token", is("ABCDEFGH"))))));
    }

    @Test
    void getMeta() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/meta.json")));
        StepVerifier.create(tinderClient.getMeta(authToken))
                .expectNext(new TinderGetMetaResponseDto(
                        new TinderUserDto(
                                "52b4d9ed6c5685412c0002a1",
                                "My description",
                                ZonedDateTime.of(1989, 2, 27, 12, 43, 19, 465_000_000, ZoneId.of("UTC")),
                                "JuL",
                                List.of(
                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5b6329d7f84145486ecaf51a/original_55965a96-5239-4f18-bb3e-8c87d374c5fd.png")))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/meta"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getProfile() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/profile.json")));
        StepVerifier.create(tinderClient.getProfile(authToken))
                .expectNext(new TinderProfileResponseDto(
                        new TinderProfileDto(
                                new TinderProfileLikesDto(2))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/v2/profile")),
                        query("include", is("likes")))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getRecommendations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/recs_core.json")));
        StepVerifier.create(tinderClient.getRecommendations(authToken))
                .expectNext(new TinderGetRecommendationsResponseDto(
                        new TinderGetRecommendationsDataResponseDto(List.of(
                                new TinderRecommendationDto(
                                        new TinderUserDto(
                                                "5c309d64cbb73e636034dc15",
                                                "",
                                                ZonedDateTime.of(1991, 1, 8, 17, 18, 24, 39_000_000, ZoneId.of("UTC")),
                                                "Laetitia",
                                                List.of(
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_3cfd9990-392c-42c4-8c70-dae8fa620930.jpg"),
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5c309d64cbb73e636034dc15/1080x1080_496f518b-a369-4740-8d95-f19bdb2a3572.jpg")))),
                                new TinderRecommendationDto(
                                        new TinderUserDto(
                                                "5c30dbfb7f49061862de6256",
                                                "Steuplé, Fais moi rire !",
                                                ZonedDateTime.of(1991, 1, 8, 17, 18, 24, 39_000_000, ZoneId.of("UTC")),
                                                "Florine",
                                                List.of(
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5c30dbfb7f49061862de6256/1080x1080_8b718deb-9c11-4835-86e7-100c613f865e.jpg"))))))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/v2/recs/core"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void passUser() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/pass.json")));
        StepVerifier.create(tinderClient.passUser(authToken, "5b884aa3b251058d4b7d46f3"))
                .expectNext(new TinderPassResponseDto())
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/pass/5b884aa3b251058d4b7d46f3"))),
                hasRequiredOauthHeader)));
    }

    @Nested
    class likeUser {
        @Test
        void notMatched() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_not-matched.json")));
            StepVerifier.create(tinderClient.likeUser(authToken, "5b884aa3b251058d4b7d46f3"))
                    .expectNext(new TinderLikeResponseDto(false))
                    .verifyComplete();
            assertThat(server, takingRequest(allOf(
                    methodSpring(is(GET)),
                    uri(path(is("/like/5b884aa3b251058d4b7d46f3"))),
                    hasRequiredOauthHeader)));
        }

        @Test
        void matched() {
            server.enqueue(new MockResponse()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_matched.json")));
            StepVerifier.create(tinderClient.likeUser(authToken, "5b884aa3b251058d4b7d46f3"))
                    .assertNext(it -> assertThat(it.getMatch(), is(not(false))))
                    .verifyComplete();
        }

        @Test
        void quotaExceeded() {
            server.enqueue(new MockResponse()
                    // TODO .setResponseCode()
                    .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/like_like-remaining.json")));
            StepVerifier.create(tinderClient.likeUser(authToken, "5b884aa3b251058d4b7d46f3"))
                    .expectNext(new TinderLikeResponseDto(false))
                    .verifyComplete();
        }
    }

    @Test
    void getMatches() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches.json")));
        StepVerifier.create(tinderClient.getMatches(authToken))
                .expectNext(new TinderGetConversationResponseDto(
                        new TinderGetConversationDataResponseDto(List.of(
                                new TinderMatchDto(
                                        "52b4d9ed6c5685412c0002a15bcc1a0cf51fe1f73b780558",
                                        List.of(),
                                        new TinderUserDto(
                                                "5bcc1a0cf51fe1f73b780558",
                                                null, // absent
                                                ZonedDateTime.of(1987, 11, 2, 22, 1, 49, 810_000_000, ZoneId.of("UTC")),
                                                "Marie",
                                                List.of(
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5bcc1a0cf51fe1f73b780558/1080x1080_3ea65ee2-33e1-4a8d-8860-73a7cf2a0a7c.jpg"),
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5bcc1a0cf51fe1f73b780558/1080x1080_d699cc0f-0f82-4add-8da9-8da8df4b39ca.jpg"))),
                                        ZonedDateTime.of(2018, 10, 29, 14, 44, 50, 422_000_000, ZoneId.of("UTC"))),
                                new TinderMatchDto(
                                        "52b4d9ed6c5685412c0002a15aafa40957398e766afe7ed4",
                                        List.of(
                                                new TinderMessageDto(
                                                        "5bd2e800e46c4dd64d1a0866",
                                                        "52b4d9ed6c5685412c0002a1",
                                                        "5aafa40957398e766afe7ed4",
                                                        "Alors les entretiens RH sur Tinder, ça embuache ? 😋",
                                                        ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500_000_000, ZoneId.of("UTC")))),
                                        new TinderUserDto(
                                                "5aafa40957398e766afe7ed4",
                                                null, // absent
                                                ZonedDateTime.of(1988, 11, 2, 22, 1, 49, 810_000_000, ZoneId.of("UTC")),
                                                "Alexandra",
                                                List.of(
                                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5aafa40957398e766afe7ed4/1080x1080_e13be39b-a619-427b-8359-7337e04250f3.jpg"))),
                                        ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500_000_000, ZoneId.of("UTC")))))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/v2/matches")),
                        query("count", is("60")))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getMessagesForMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_messages.json")));
        StepVerifier.create(tinderClient.getMessagesForMatch(authToken, "52b4d9ed6c5685412c0002a15aafa40957398e766afe7ed4"))
                .expectNext(new TinderGetMessagesResponseDto(
                        new TinderGetMessagesDataResponseDto(List.of(
                                new TinderMessageDto(
                                        "5bd2e800e46c4dd64d1a0866",
                                        "52b4d9ed6c5685412c0002a1",
                                        "5aafa40957398e766afe7ed4",
                                        "Alors les entretiens RH sur Tinder, ça embuache ? 😋",
                                        ZonedDateTime.of(2018, 10, 26, 10, 10, 8, 500_000_000, ZoneId.of("UTC"))),
                                new TinderMessageDto(
                                        "5bd2e7e67cd91a673b1a9873",
                                        "5aafa40957398e766afe7ed4",
                                        "5aafa40957398e766afe7ed4",
                                        "Bonjour Julien",
                                        ZonedDateTime.of(2018, 10, 26, 10, 9, 42, 830_000_000, ZoneId.of("UTC")))))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/v2/matches/52b4d9ed6c5685412c0002a15aafa40957398e766afe7ed4/messages")),
                        query("count", is("100")))),
                hasRequiredOauthHeader)));
    }

    @Test
    void sendMessageToMatch() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/matches_sendMessage.json")));
        StepVerifier.create(tinderClient.sendMessageToMatch(authToken, "5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530", "Et perso je fonctionne beaucoup au feeling"))
                .expectNext(new TinderSendMessageResponseDto(
                        "5c157885c5ee3f110007b6fc",
                        ZonedDateTime.of(2018, 12, 15, 21, 56, 21, 322_000_000, ZoneId.of("UTC")),
                        "Et perso je fonctionne beaucoup au feeling"))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/user/matches/5b6329d7f84145486ecaf51a5c1412e7f3902fed66dd3530"))),
                hasRequiredOauthHeader,
                body(hasJsonPath("$.message", is("Et perso je fonctionne beaucoup au feeling"))))));
    }

    @Test
    void getUser() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user.json")));
        StepVerifier.create(tinderClient.getUser(authToken, "5b486956f408df634d26de3b"))
                .expectNext(new TinderGetUserResponseDto(
                        new TinderUserDto(
                                "5b486956f408df634d26de3b",
                                "My description",
                                ZonedDateTime.of(1988, 11, 2, 22, 5, 24, 419_000_000, ZoneId.of("UTC")),
                                "Al",
                                List.of(
                                        new TinderPhotoDto("https://images-ssl.gotinder.com/5b486956f408df634d26de3b/1080x1080_7cd50312-0063-4814-89b0-1568886056ba.jpg")))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/user/5b486956f408df634d26de3b"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void ping() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/tinder/user__ping.json")));
        StepVerifier.create(tinderClient.ping(authToken, 48.8534, 2.3488))
                .expectNext(new TinderPingResponseDto("200"))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/user/ping"))),
                hasRequiredOauthHeader,
                body(allOf(
                        hasJsonPath("$.lat", is(48.8534)),
                        hasJsonPath("$.lon", is(2.3488)))))));
    }
}
