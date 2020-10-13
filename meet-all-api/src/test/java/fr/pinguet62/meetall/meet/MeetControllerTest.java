package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.PartialArrayList;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.security.utils.WithMockUserId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.meet.MeetControllerTest.currentUserId;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

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
    private MeetService meetService;

    @Test
    public void getProposals() {
        when(meetService.getProposalsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(List.of(
                new ProposalDto("proposal-1", new ProfileDto("profile-id-1", "profile-name-1", 1, emptyList())),
                new ProposalDto("proposal-2", new ProfileDto("profile-id-2", "profile-name-2", 2, emptyList()))))));

        webTestClient
                .get()
                .uri("/proposals")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("proposal-1")
                .jsonPath("$[1].id").isEqualTo("proposal-2");
    }

    @Test
    public void getProposals_partial() {
        when(meetService.getProposalsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(emptyList(), true)));

        webTestClient
                .get()
                .uri("/proposals")
                .exchange()
                .expectStatus().isEqualTo(PARTIAL_CONTENT);
    }

    @Test
    public void unlikeProposal() {
        final String credentialId = "42";
        final String id = "99";

        when(meetService.likeOrUnlikeProposal(currentUserId, credentialId, id, false)).thenReturn(Mono.empty());

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/proposals").pathSegment(transformedId).pathSegment("unlike").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        verify(meetService).likeOrUnlikeProposal(currentUserId, credentialId, id, false);
    }

    @Test
    public void likeProposal() {
        final String credentialId = "42";
        final String id = "99";

        when(meetService.likeOrUnlikeProposal(currentUserId, credentialId, id, true)).thenReturn(Mono.just(true));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/proposals").pathSegment(transformedId).pathSegment("like").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Boolean.class).isEqualTo(true);

        verify(meetService).likeOrUnlikeProposal(currentUserId, credentialId, id, true);
    }

    @Test
    public void getConversations() {
        when(meetService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(List.of(
                new ConversationDto("conversation-1", new ProfileDto("profile-id-1", "profile-name-1", 1, emptyList()), now(), new MessageDto("message-1", now(), true, "message-text-1")),
                new ConversationDto("conversation-2", new ProfileDto("profile-id-2", "profile-name-2", 2, emptyList()), now(), new MessageDto("message-2", now(), false, "message-text-2"))), false)));

        webTestClient
                .get()
                .uri("/conversations")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("conversation-1")
                .jsonPath("$[1].id").isEqualTo("conversation-2");
    }

    @Test
    public void getConversations_partial() {
        when(meetService.getConversationsForUser(currentUserId)).thenReturn(Mono.just(new PartialArrayList<>(emptyList(), true)));

        webTestClient
                .get()
                .uri("/conversations")
                .exchange()
                .expectStatus().isEqualTo(PARTIAL_CONTENT);
    }

    @Test
    public void getMessages() {
        final String credentialId = "42";
        final String id = "99";

        when(meetService.getMessagesForUser(currentUserId, credentialId, id)).thenReturn(Flux.fromIterable(List.of(
                new MessageDto("message-1", now(), true, "message-text-1"),
                new MessageDto("message-2", now(), false, "message-text-2"))));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/conversations").pathSegment(transformedId).pathSegment("messages").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("message-1")
                .jsonPath("$[1].id").isEqualTo("message-2");
    }

    @Test
    public void sendMessage() {
        final String credentialId = "42";
        final String id = "99";
        final String text = "text";

        when(meetService.sendMessage(currentUserId, credentialId, id, text)).thenReturn(Mono.just(new MessageDto("message-9", now(), true, text)));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path("/conversations").pathSegment(transformedId).pathSegment("message").build())
                .bodyValue(text)
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
        final String credentialId = "42";
        final String id = "99";

        when(meetService.getProfileForUser(currentUserId, credentialId, id)).thenReturn(Mono.just(new ProfileDto("profile-id", "profile-name", 29, emptyList())));

        String transformedId = TransformedId.format(credentialId, id);
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/profile").pathSegment(transformedId).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("profile-id")
                .jsonPath("$.name").isEqualTo("profile-name")
                .jsonPath("$.age").isEqualTo("29")
                .jsonPath("$.avatars").isEmpty();
    }

    @Test
    public void setPosition() {
        when(meetService.setPosition(ArgumentMatchers.eq(currentUserId), eq(48.8534, 0.0001), eq(2.3488, 0.0001), eq(35.1, 0.0001))).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/position?latitude=48.8534&longitude=2.3488&altitude=35.1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

}
