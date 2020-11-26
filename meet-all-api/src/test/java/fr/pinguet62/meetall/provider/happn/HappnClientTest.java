package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto.HappnConversationDto.HappnParticipantDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDeviceResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDeviceResponseDto.HappnDeviceDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnDevicesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessagesResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto.HappnNotificationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnOauthResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto.HappnRecommendationContentDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto.HappnRecommendationContentDto.HappnUserV1Dto;
import fr.pinguet62.meetall.provider.happn.dto.HappnSendMessageResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserAcceptedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserAcceptedResponseDto.HappnUserAcceptedDataDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnProfileDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserRejectedResponseDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserRejectedResponseDto.HappnUserRejectedDataDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserResponseDto;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;
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
import static fr.pinguet62.meetall.provider.happn.HappnClient.HEADER;
import static fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnRelation.ACCEPTED_BY_ME;
import static fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnRelation.NEW_RELATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class HappnClientTest {

    final String authToken = "xxx.yyy.zzz";
    Matcher<RecordedRequest> hasRequiredOauthHeader = header(HEADER, "OAuth=\"" + authToken + "\"");

    MockWebServer server;
    HappnClient happnClient;

    @BeforeEach
    void startServer() {
        server = new MockWebServer();
        happnClient = new HappnClient(WebClient.builder(), server.url("/").toString());
    }

    @AfterEach
    void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void getOptions() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/api.json")));
        StepVerifier.create(happnClient.getOptions())
                .assertNext(it -> {
                    assertThat(it.isSuccess(), is(true));
                    assertThat(it.getStatus(), is(200));
                })
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(OPTIONS)),
                uri(path(is("/api"))))));
    }

    @Test
    void connectOauthToken() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/connect__oauth__token.json")));
        StepVerifier.create(happnClient.connectOauthToken("ABCDEFGH"))
                .expectNext(new HappnOauthResponseDto("eyJhbGciOiJIUzI1NiJ9.eyJzY29wZSI6WyJ1c2VyX2RlbGV0ZSIsInVzZXJfcmVwb3J0X3JlYWQiLCJ1c2VyX3RyYWl0X2Fuc3dlcl93cml0ZSIsInVzZXJfdXBkYXRlIiwiYm9vc3RfY3JlYXRlIiwidXNlcl9hdmFpbGFiaWxpdHlfY3JlYXRlIiwiYm9vc3RfcmVhZCIsInVzZXJfc29jaWFsX2RlbGV0ZSIsInVzZXJfY29udmVyc2F0aW9uX3JlYWQiLCJ1c2VyX2FjY2VwdGVkX3JlYWQiLCJ1c2VyX2NvbnZlcnNhdGlvbl9jcmVhdGUiLCJhbGxfdXNlcl90cmFpdF9hbnN3ZXJfcmVhZCIsInVzZXJfcmVqZWN0ZWRfY3JlYXRlIiwidXNlcl9ibG9ja2VkX3JlYWQiLCJ1c2VyX3JlcG9ydF91cGRhdGUiLCJ1c2VyX2FjaGlldmVtZW50X3JlYWQiLCJ1c2VyX2F2YWlsYWJpbGl0eV9yZWFkIiwidXNlcl9hY2hpZXZlbWVudF9kZWxldGUiLCJ1c2VyX3JlcG9ydF9kZWxldGUiLCJ1c2VyX2FjaGlldmVtZW50X3VwZGF0ZSIsInVzZXJfdmlkZW9jYWxsX3VwZGF0ZSIsInVzZXJfYXBwbGljYXRpb25zX3JlYWQiLCJ1c2VyX2Jsb2NrZWRfZGVsZXRlIiwidXNlcl9zdWJzY3JpcHRpb25fY3JlYXRlIiwicGFja19yZWFkIiwidXNlcl9vcmRlcl91cGRhdGUiLCJ1c2VyX3JlYWQiLCJub3RpZmljYXRpb25fdHlwZV9yZWFkIiwidXNlcl9hY2hpZXZlbWVudF9jcmVhdGUiLCJ1c2VyX21lc3NhZ2VfcmVhZCIsInVzZXJfaW1hZ2VfY3JlYXRlIiwidXNlcl9jb252ZXJzYXRpb25fZGVsZXRlIiwidXNlcl9zb2NpYWxfdXBkYXRlIiwidXNlcl9kZXZpY2VfZGVsZXRlIiwidXNlcl9hY2NlcHRlZF9jcmVhdGUiLCJ1c2VyX2F2YWlsYWJpbGl0eV91cGRhdGUiLCJzdWJzY3JpcHRpb25fdHlwZV9yZWFkIiwidXNlcl9wb2tlX2NyZWF0ZSIsInRyYWl0X3JlYWQiLCJ1c2VyX2FwcGxpY2F0aW9uc191cGRhdGUiLCJ1c2VyX3JlcG9ydF9jcmVhdGUiLCJ1c2VyX29yZGVyX2NyZWF0ZSIsInVzZXJfZGV2aWNlX3VwZGF0ZSIsInVzZXJfc2hvcF9yZWFkIiwiYXJjaGl2ZV9jcmVhdGUiLCJ1c2VyX3JlamVjdGVkX3JlYWQiLCJ1c2VyX2FwcGxpY2F0aW9uc19kZWxldGUiLCJ1c2VyX3N1YnNjcmlwdGlvbl9kZWxldGUiLCJ1c2VyX3N1YnNjcmlwdGlvbl9yZWFkIiwidXNlcl92aWRlb2NhbGxfcmVhZCIsInVzZXJfYmxvY2tlZF9jcmVhdGUiLCJ1c2VyX3N1YnNjcmlwdGlvbl91cGRhdGUiLCJ1c2VyX21lc3NhZ2VfY3JlYXRlIiwidXNlcl9tZXNzYWdlX2RlbGV0ZSIsInVzZXJfbW9kZV9yZWFkIiwidXNlcl9zb2NpYWxfY3JlYXRlIiwidXNlcl9pbWFnZV91cGRhdGUiLCJsb2NhbGVfcmVhZCIsInVzZXJfbm90aWZpY2F0aW9uc19yZWFkIiwiYWNoaWV2ZW1lbnRfdHlwZV9yZWFkIiwic2VhcmNoX3VzZXIiLCJ1c2VyX2ltYWdlX2RlbGV0ZSIsInVzZXJfZGV2aWNlX3JlYWQiLCJhbGxfdXNlcl9yZWFkIiwidXNlcl9zb2NpYWxfcmVhZCIsImFyY2hpdmVfcmVhZCIsInVzZXJfZGV2aWNlX2NyZWF0ZSIsInVzZXJfcG9zaXRpb25fcmVhZCIsInVzZXJfYWNjZXB0ZWRfZGVsZXRlIiwidXNlcl9tZXNzYWdlX3VwZGF0ZSIsInVzZXJfb3JkZXJfcmVhZCIsInVzZXJfdmlkZW9jYWxsX2NyZWF0ZSIsImxhbmd1YWdlX3JlYWQiLCJ1c2VyX2F2YWlsYWJpbGl0eV9kZWxldGUiLCJhbGxfaW1hZ2VfcmVhZCIsInVzZXJfY29udmVyc2F0aW9uX3VwZGF0ZSIsInVzZXJfaW1hZ2VfcmVhZCIsInVzZXJfcmVqZWN0ZWRfZGVsZXRlIiwicmVwb3J0X3R5cGVfcmVhZCIsInVzZXJfcG9zaXRpb25fdXBkYXRlIl0sImp0aSI6IjM2ODM3MTVkLTc4OTgtNGY3MC1iODVjLTQ0ZWE2YTRmYTA0MCIsInN1YiI6ImRiMTdmZWNiLTYzMzItNGJiMi1hNWFkLTJhZDMzMTdkNGFmMyIsImF1ZCI6IkZVRS1pZFNFUC1mN0FxQ3l1TWNQcjJLLTFpQ0lVX1lsdkstTS1pbTNjIiwiZXhwIjoxNjA2NDgzNTk0LCJpYXQiOjE2MDYzOTcxOTR9.r9jtIE-gr3On7K4Jwt9_He2782i7pWkczn4je8LqDmw"))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/connect/oauth/token"))),
                body(containsString("ABCDEFGH")))));
    }

    @Test
    void getNotifications() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/notifications.json")));
        StepVerifier.create(happnClient.getNotifications(authToken))
                .expectNext(new HappnNotificationsResponseDto(List.of(
                        new HappnNotificationDto(
                                new HappnUserDto(
                                        "1489726553",
                                        "CLIENT",
                                        "Virginie",
                                        NEW_RELATION,
                                        null, // absent
                                        26,
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/1489726553/320-320.0_592356f68d916-592356f68d95e.jpg"),
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/1489726553/320-320.0_57c80a2f98d74-57c80a2f98dc7.jpg")),
                                        null)),
                        new HappnNotificationDto(
                                new HappnUserDto(
                                        "21086711847",
                                        "CLIENT",
                                        "Pub",
                                        ACCEPTED_BY_ME,
                                        null, // absent
                                        0,
                                        null, // absent
                                        List.of(),
                                        null)),
                        new HappnNotificationDto(
                                new HappnUserDto(
                                        "1eeeb72a-6ae8-4d0f-aea7-68a0852d5063",
                                        "CLIENT",
                                        "Takoua",
                                        NEW_RELATION,
                                        null, // absent
                                        26,
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/1eeeb72a-6ae8-4d0f-aea7-68a0852d5063/320-320.0_b0ec72e0-bd1c-11e8-9958-372c0714599a.jpg")),
                                        null)),
                        new HappnNotificationDto(
                                new HappnUserDto(
                                        "895c50d5-ac5c-4625-877e-44375c0af158",
                                        "CLIENT",
                                        "Chera",
                                        NEW_RELATION,
                                        null, // absent
                                        32,
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://images.happn.fr/resizing/895c50d5-ac5c-4625-877e-44375c0af158/42f041e0-0acc-11eb-8d03-eb8aa831a0b7.jpg?width=320&height=320&mode=0"),
                                                new HappnProfileDto("https://images.happn.fr/resizing/895c50d5-ac5c-4625-877e-44375c0af158/43c740f0-0acc-11eb-ad13-ad9bf2f10c95.jpg?width=320&height=320&mode=0")),
                                        null)))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/api/users/me/notifications")),
                        query("types", is("468")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getRecommendations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/recommendations.json")));
        StepVerifier.create(happnClient.getRecommendations(authToken, "1deda3d7-3dff-498a-95f6-2d2c0e347ea7"))
                .expectNext(new HappnRecommendationsResponseDto(List.of(
                        new HappnRecommendationDto(
                                new HappnRecommendationContentDto(
                                        new HappnUserV1Dto(
                                                "c9009c9e-6d62-4b77-898f-315421083077",
                                                "Ahlam",
                                                "",
                                                29,
                                                List.of(
                                                        new HappnProfileDto("https://images.happn.fr/resizing/c9009c9e-6d62-4b77-898f-315421083077/764563f0-2a88-11eb-9961-8329ff84826d.jpg?width=640&height=1136&mode=1"))))),
                        new HappnRecommendationDto(
                                new HappnRecommendationContentDto(
                                        new HappnUserV1Dto(
                                                "16002878-5c89-4695-9db9-7685a47a68db",
                                                "Tessa",
                                                "NL ( vlaams) zwevegem",
                                                28,
                                                List.of(
                                                        new HappnProfileDto("https://images.happn.fr/resizing/16002878-5c89-4695-9db9-7685a47a68db/49f929e0-2c94-11eb-be94-935c09c71dac.jpg?width=640&height=1136&mode=1"),
                                                        new HappnProfileDto("https://images.happn.fr/resizing/16002878-5c89-4695-9db9-7685a47a68db/4a6ad770-2c94-11eb-be94-935c09c71dac.jpg?width=640&height=1136&mode=1"),
                                                        new HappnProfileDto("https://images.happn.fr/resizing/16002878-5c89-4695-9db9-7685a47a68db/4abf3900-2c94-11eb-8e9b-2346a4aaaa45.jpg?width=640&height=1136&mode=1"))))),
                        new HappnRecommendationDto(
                                new HappnRecommendationContentDto(
                                        new HappnUserV1Dto(
                                                "9e260706-6693-4d76-b522-f16deef69116",
                                                "Iona",
                                                "",
                                                20,
                                                List.of(
                                                        new HappnProfileDto("https://images.happn.fr/resizing/9e260706-6693-4d76-b522-f16deef69116/d63c03b0-2f18-11eb-b7ce-47e6af6a6a6c.jpg?width=640&height=1136&mode=1"),
                                                        new HappnProfileDto("https://images.happn.fr/resizing/9e260706-6693-4d76-b522-f16deef69116/d7d5dd90-2f18-11eb-9cf3-33c094b96fd0.jpg?width=640&height=1136&mode=1"))))))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/api/v1/users/me/recommendations")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader,
                header("x-happn-did", "1deda3d7-3dff-498a-95f6-2d2c0e347ea7"))));
    }

    @Test
    void rejectedUser() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "success": true,
                          "status": 200,
                          "error": null,
                          "data": {
                            "message": "user rejected"
                          },
                          "error_code": 0
                        }
                        """));
        StepVerifier.create(happnClient.rejectedUser(authToken, "895c50d5-ac5c-4625-877e-44375c0af158"))
                .expectNext(new HappnUserRejectedResponseDto(
                        new HappnUserRejectedDataDto()))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/api/users/me/rejected/895c50d5-ac5c-4625-877e-44375c0af158"))),
                hasRequiredOauthHeader)));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void acceptedUser(boolean matched) {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody("""
                        {
                          "success": true,
                          "status": 200,
                          "error": null,
                          "data": {
                            "message": "user accepted",
                            "has_crushed": """ + matched + """
                          },
                          "error_code": 0
                        }
                        """));
        StepVerifier.create(happnClient.acceptedUser(authToken, "895c50d5-ac5c-4625-877e-44375c0af158"))
                .expectNext(new HappnUserAcceptedResponseDto(
                        new HappnUserAcceptedDataDto(matched)))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(path(is("/api/users/me/accepted/895c50d5-ac5c-4625-877e-44375c0af158"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getConversations() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/conversations.json")));
        StepVerifier.create(happnClient.getConversations(authToken))
                .expectNext(new HappnConversationsResponseDto(List.of(
                        new HappnConversationDto(
                                "6758b9e9-7691-4acb-8096-9a8c596098dc_7339ddb1-72d9-468d-b3b6-8bf84f8f9555",
                                List.of(
                                        new HappnParticipantDto(
                                                new HappnUserDto(
                                                        "6758b9e9-7691-4acb-8096-9a8c596098dc",
                                                        "CLIENT",
                                                        "Julien",
                                                        NEW_RELATION,
                                                        null, // absent
                                                        29,
                                                        null, // absent
                                                        List.of(
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/6758b9e9-7691-4acb-8096-9a8c596098dc/320-320.0_69f1eca0-db4c-11e8-ba9e-65e7cf8878f0.jpg")),
                                                        LocalDate.parse("1989-06-14"))),
                                        new HappnParticipantDto(
                                                new HappnUserDto(
                                                        "7339ddb1-72d9-468d-b3b6-8bf84f8f9555",
                                                        "CLIENT",
                                                        "Alice",
                                                        NEW_RELATION,
                                                        null, // absent
                                                        29,
                                                        null, // absent
                                                        List.of(
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/7339ddb1-72d9-468d-b3b6-8bf84f8f9555/320-320.0_1c7dc000-e27e-11e8-99cf-737224347269.jpg")),
                                                        null))), // hidden
                                List.of(),
                                OffsetDateTime.parse("2019-01-06T16:09:27+00:00")),
                        new HappnConversationDto(
                                "6758b9e9-7691-4acb-8096-9a8c596098dc_93833b7c-a427-45be-b3cd-81d068108184",
                                List.of(
                                        new HappnParticipantDto(
                                                new HappnUserDto(
                                                        "6758b9e9-7691-4acb-8096-9a8c596098dc",
                                                        "CLIENT",
                                                        "Julien",
                                                        NEW_RELATION,
                                                        null, // absent
                                                        29,
                                                        null, // absent
                                                        List.of(
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/6758b9e9-7691-4acb-8096-9a8c596098dc/320-320.0_69f1eca0-db4c-11e8-ba9e-65e7cf8878f0.jpg")),
                                                        LocalDate.parse("1989-06-14"))),
                                        new HappnParticipantDto(
                                                new HappnUserDto(
                                                        "93833b7c-a427-45be-b3cd-81d068108184",
                                                        "CLIENT",
                                                        "Cha",
                                                        NEW_RELATION,
                                                        null, // absent
                                                        30,
                                                        null, // absent
                                                        List.of(
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7b745d40-aed9-11e8-9d0d-0f272e08b1ea.jpg"),
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7c213f10-aed9-11e8-a9a7-0d1f7e69a1ae.jpg")),
                                                        null))), // hidden
                                List.of(
                                        new HappnMessageDto(
                                                "1543139278_30e2ad90-f097-11e8-b59a-bf3837b6acfb",
                                                "Bonjour, comment vas tu ? :)",
                                                OffsetDateTime.parse("2018-11-25T09:47:58+00:00"),
                                                new HappnUserDto(
                                                        "93833b7c-a427-45be-b3cd-81d068108184",
                                                        "CLIENT",
                                                        null, // absent
                                                        null, // absent
                                                        null, // absent
                                                        null, // absent
                                                        null, // absent
                                                        List.of(
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7b745d40-aed9-11e8-9d0d-0f272e08b1ea.jpg"),
                                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/93833b7c-a427-45be-b3cd-81d068108184/320-320.0_7c213f10-aed9-11e8-a9a7-0d1f7e69a1ae.jpg")),
                                                        null))), // absent
                                OffsetDateTime.parse("2018-11-25T09:47:58+00:00")))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/api/users/me/conversations")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getConversationMessages() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/messages.json")));
        StepVerifier.create(happnClient.getConversationMessages(authToken, "6758b9e9-7691-4acb-8096-9a8c596098dc_7339ddb1-72d9-468d-b3b6-8bf84f8f9555"))
                .expectNext(new HappnMessagesResponseDto(List.of(
                        new HappnMessageDto(
                                "1542396170_021ed100-e9d5-11e8-97b3-21468353071b",
                                "Salut, ça va et toi ? Pour être sincère avec toi, j’ai passé une bonne soirée, on a bien papoté mais je n’ai pas eu le petit truc...",
                                OffsetDateTime.parse("2018-11-16T19:22:50+00:00"),
                                new HappnUserDto(
                                        "21149123737",
                                        "CLIENT",
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/21149123737/320-320.0_57f50f57c0ac1-57f50f57c0b06.jpg"),
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/21149123737/320-320.0_57f50c985fa81-57f50c985fac0.jpg")),
                                        null)), // absent (& hidden ?)
                        new HappnMessageDto(
                                "1542395520_7e855a40-e9d3-11e8-97b3-21468353071b",
                                "Ah compris : fameux tiers des personnes pas très sincères qui n'envoient plus de message.",
                                OffsetDateTime.parse("2018-11-16T19:12:00+00:00"),
                                new HappnUserDto(
                                        "e9644408-76ca-4599-8106-e9e0368bdd1c",
                                        "CLIENT",
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/e9644408-76ca-4599-8106-e9e0368bdd1c/320-320.0_68261050-c1b0-11e8-b57d-0fc094ce31f9.jpg")),
                                        LocalDate.parse("1989-06-14"))))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/api/conversations/6758b9e9-7691-4acb-8096-9a8c596098dc_7339ddb1-72d9-468d-b3b6-8bf84f8f9555/messages")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader)));
    }

    @Test
    void sendConversationMessage() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/sendMessage.json")));
        StepVerifier.create(happnClient.sendConversationMessage(authToken, "6758b9e9-7691-4acb-8096-9a8c596098dc_7339ddb1-72d9-468d-b3b6-8bf84f8f9555", "test"))
                .expectNext(new HappnSendMessageResponseDto(
                        new HappnMessageDto(
                                "1545053021_f867e470-01fe-11e9-8e3d-ab647b2d0947",
                                "test",
                                OffsetDateTime.parse("2018-12-17T13:23:41+00:00"),
                                new HappnUserDto(
                                        "6758b9e9-7691-4acb-8096-9a8c596098dc",
                                        "CLIENT",
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        null, // absent
                                        List.of(
                                                new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/6758b9e9-7691-4acb-8096-9a8c596098dc/320-320.0_69f1eca0-db4c-11e8-ba9e-65e7cf8878f0.jpg")),
                                        LocalDate.parse("1989-06-14")))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(POST)),
                uri(allOf(
                        path(is("/api/conversations/6758b9e9-7691-4acb-8096-9a8c596098dc_7339ddb1-72d9-468d-b3b6-8bf84f8f9555/messages")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader,
                body(hasJsonPath("$.message", is("test"))))));
    }

    @Test
    void getUser() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/users.json")));
        StepVerifier.create(happnClient.getUser(authToken, "48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1"))
                .expectNext(new HappnUserResponseDto(
                        new HappnUserDto(
                                "48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1",
                                "CLIENT",
                                "Sophie",
                                ACCEPTED_BY_ME,
                                null, // absent
                                26,
                                "Je cherche l'amour...",
                                List.of(
                                        new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/320-320.0_559a0790-d18a-11e8-9e28-196ec4dfc48e.jpg"),
                                        new HappnProfileDto("https://1675564c27.optimicdn.com/cache/images/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/320-320.0_3219f290-c117-11e8-b57d-0fc094ce31f9.jpg")),
                                null))) // hard (& hidden)
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(allOf(
                        path(is("/api/users/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1")),
                        query("fields", notNullValue()))),
                hasRequiredOauthHeader)));
    }

    @Test
    void getUserDevices() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/users_me_devices.json")));
        StepVerifier.create(happnClient.getUserDevices(authToken, "48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1"))
                .expectNext(new HappnDevicesResponseDto(List.of(
                        new HappnDeviceDto("87fbaa4f-2cef-4c60-bac3-08348cdf32e5"))))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(GET)),
                uri(path(is("/api/users/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/devices"))),
                hasRequiredOauthHeader)));
    }

    @Test
    void setUserPosition() {
        server.enqueue(new MockResponse()
                .setHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .setBody(readResource("/fr/pinguet62/meetall/provider/happn/users_me_devices_position.json")));
        StepVerifier.create(happnClient.setUserPosition(authToken, "48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1", "87fbaa4f-2cef-4c60-bac3-08348cdf32e5", 48.8534, 2.3488, 35.1))
                .expectNext(new HappnDeviceResponseDto(
                        new HappnDeviceDto(
                                "87fbaa4f-2cef-4c60-bac3-08348cdf32e5")))
                .verifyComplete();
        assertThat(server, takingRequest(allOf(
                methodSpring(is(PUT)),
                uri(path(is("/api/users/48a3216c-a73f-4f7b-a5f9-aeeacdbabeb1/devices/87fbaa4f-2cef-4c60-bac3-08348cdf32e5"))),
                hasRequiredOauthHeader,
                body(allOf(
                        hasJsonPath("$.latitude", is(48.8534)),
                        hasJsonPath("$.longitude", is(2.3488)),
                        hasJsonPath("$.alt", is(35.1)))))));
    }
}
