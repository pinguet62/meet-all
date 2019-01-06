package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.utils.WithMockUserId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.MeetControllerTest.currentUserId;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@RunWith(SpringRunner.class)
@WebFluxTest(MeetController.class)
@WithMockUserId(currentUserId)
public class MeetControllerTest {

    /**
     * @see WithMockUserId
     */
    static final String currentUserId = "userId";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProvidersService providersService;

    @Test
    public void getProposals() {
        when(providersService.getProposalsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(asList(
                new ProposalDto("proposal-1", new ProfileDto("profile-id-1", "profile-name-1", 1, emptyList())),
                new ProposalDto("proposal-2", new ProfileDto("profile-id-2", "profile-name-2", 2, emptyList()))))));

        webTestClient.get()
                .uri("/proposals")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("proposal-1")
                .jsonPath("$[1].id").isEqualTo("proposal-2");
    }

    @Test
    public void getProposals_partial() {
        when(providersService.getProposalsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(emptyList(), true)));

        webTestClient.get()
                .uri("/proposals")
                .exchange()
                .expectStatus().isEqualTo(PARTIAL_CONTENT);
    }

    @Test
    public void getConversations() {
        when(providersService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(asList(
                new ConversationDto("conversation-1", new ProfileDto("profile-id-1", "profile-name-1", 1, emptyList()), now(), new MessageDto("message-1", now(), true, "message-text-1")),
                new ConversationDto("conversation-2", new ProfileDto("profile-id-2", "profile-name-2", 2, emptyList()), now(), new MessageDto("message-2", now(), false, "message-text-2"))), false)));

        webTestClient.get()
                .uri("/conversations")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("conversation-1")
                .jsonPath("$[1].id").isEqualTo("conversation-2");
    }

    @Test
    public void getConversations_partial() {
        when(providersService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(emptyList(), true)));

        webTestClient.get()
                .uri("/conversations")
                .exchange()
                .expectStatus().isEqualTo(PARTIAL_CONTENT);
    }

    @Test
    public void getMessages() {
        final int credentialId = 42;
        final String id = "99";

        when(providersService.getMessagesForUser(currentUserId, credentialId, id)).thenReturn(Flux.fromIterable(asList(
                new MessageDto("message-1", now(), true, "message-text-1"),
                new MessageDto("message-2", now(), false, "message-text-2"))));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/conversations").pathSegment(transformedId).pathSegment("messages").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("message-1")
                .jsonPath("$[1].id").isEqualTo("message-2");
    }

    @Test
    public void sendMessage() {
        final int credentialId = 42;
        final String id = "99";
        final String text = "text";

        when(providersService.sendMessage(currentUserId, credentialId, id, text)).thenReturn(Mono.justOrEmpty(new MessageDto("message-9", now(), true, text)));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/conversations").pathSegment(transformedId).pathSegment("message").build())
                .body(fromObject(text))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("message-9")
                .jsonPath("$.date").isNotEmpty()
                .jsonPath("$.sent").isEqualTo(true)
                .jsonPath("$.text").isEqualTo(text);
    }

    @Test
    public void getProfile() {
        final int credentialId = 42;
        final String id = "99";

        when(providersService.getProfileForUser(currentUserId, credentialId, id)).thenReturn(Mono.justOrEmpty(new ProfileDto("profile-id", "profile-name", 29, emptyList())));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/profile").pathSegment(transformedId).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("profile-id")
                .jsonPath("$.name").isEqualTo("profile-name")
                .jsonPath("$.age").isEqualTo("29")
                .jsonPath("$.avatars").isEmpty();
    }

}
