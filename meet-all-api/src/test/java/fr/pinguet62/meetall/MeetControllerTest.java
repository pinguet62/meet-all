package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.SecurityWebFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.lang.String.valueOf;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;

@RunWith(SpringRunner.class)
@WebFluxTest(MeetController.class)
@Import(SecurityWebFilter.class)
public class MeetControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProvidersService providersService;

    @Test
    public void getConversations() {
        final int currentUserId = 3;

        when(providersService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(asList(
                new ConversationDto("conversation-1", new ProfileDto("profile-id-1", "profile-name-1", 1, emptyList()), now(), new MessageDto("message-1", now(), true, "message-text-1")),
                new ConversationDto("conversation-2", new ProfileDto("profile-id-2", "profile-name-2", 2, emptyList()), now(), new MessageDto("message-2", now(), false, "message-text-2"))), false)));

        webTestClient.get()
                .uri("/conversations")
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("conversation-1")
                .jsonPath("$[1].id").isEqualTo("conversation-2");
    }

    @Test
    public void getConversations_partial() {
        final int currentUserId = 3;

        when(providersService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(emptyList(), true)));

        webTestClient.get()
                .uri("/conversations")
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isEqualTo(PARTIAL_CONTENT);
    }

    @Test
    public void getMessages() {
        final int currentUserId = 3;
        final int credentialId = 42;
        final String id = "99";

        when(providersService.getMessagesForUser(currentUserId, credentialId, id)).thenReturn(Flux.fromIterable(asList(
                new MessageDto("message-1", now(), true, "message-text-1"),
                new MessageDto("message-2", now(), false, "message-text-2"))));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/conversations").pathSegment(transformedId).pathSegment("messages").build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("message-1")
                .jsonPath("$[1].id").isEqualTo("message-2");
    }

    @Test
    public void getProfile() {
        final int currentUserId = 3;
        final int credentialId = 42;
        final String id = "99";

        when(providersService.getProfileForUser(currentUserId, credentialId, id)).thenReturn(Mono.justOrEmpty(new ProfileDto("profile-id", "profile-name", 29, emptyList())));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/profile").pathSegment(transformedId).build())
                .header(AUTHORIZATION, valueOf(currentUserId))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("profile-id")
                .jsonPath("$.name").isEqualTo("profile-name")
                .jsonPath("$.age").isEqualTo("29")
                .jsonPath("$.avatars").isEmpty();
    }

}
