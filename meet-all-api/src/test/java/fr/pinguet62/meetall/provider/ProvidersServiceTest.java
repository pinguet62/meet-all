package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.PartialList;
import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.credential.CredentialRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.exception.ExpiredTokenException;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static fr.pinguet62.meetall.photoproxy.PhotoProxyEncoder.encode;
import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProvidersServiceTest {

    private CredentialRepository credentialRepository;
    private List<ProviderService> providerServices;
    private ProvidersService service;

    @Before
    public void initMocks() {
        credentialRepository = mock(CredentialRepository.class);
        providerServices = new ArrayList<>();
        service = new ProvidersService(credentialRepository, providerServices);
    }

    @Test
    public void getProposalsForUser() {
        final String userId = "userId";

        // Credentials
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(
                new Credential(91, userId, TINDER, "tinderCredential_91", "label 91"),
                new Credential(92, userId, HAPPN, "happnCredential_92", "label 92")));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getProposals("tinderCredential_91")).thenReturn(Flux.just(
                new ProposalDto("propTinder11", new ProfileDto("profTinder11", "profile name 11", 11, List.of("https://google.fr/favicon.png"))),
                new ProposalDto("propTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()))));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.getProposals("happnCredential_92")).thenReturn(Flux.just(
                new ProposalDto("propHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()))));
        providerServices.add(happnProviderService);

        Mono<PartialList<ProposalDto>> result = service.getProposalsForUser(userId);

        PartialList<ProposalDto> proposals = result.block();
        assertThat(proposals.isPartial(), is(false));
        assertThat(proposals, containsInAnyOrder(
                new ProposalDto("91#propTinder11", new ProfileDto("91#profTinder11", "profile name 11", 11, List.of(encode("https://google.fr/favicon.png")))),
                new ProposalDto("91#propTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList())),
                new ProposalDto("92#propHappn21", new ProfileDto("92#profHappn21", "profile name 21", 21, emptyList()))));
    }

    @Test
    public void getProposalsForUser_partial() {
        final String userId = "userId";

        // Credentials
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(
                new Credential(91, userId, TINDER, "tinderCredential_91", "label 91"),
                new Credential(92, userId, HAPPN, "happnCredential_92", "label 92")));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getProposals("tinderCredential_91")).thenReturn(Flux.just(
                new ProposalDto("propTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()))));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.getProposals("happnCredential_92")).thenReturn(Flux.error(new ExpiredTokenException(null)));
        providerServices.add(happnProviderService);

        Mono<PartialList<ProposalDto>> result = service.getProposalsForUser(userId);

        PartialList<ProposalDto> proposals = result.block();
        assertThat(proposals.isPartial(), is(true));
        assertThat(proposals, containsInAnyOrder(
                new ProposalDto("91#propTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList()))));
    }

    @Test
    public void getConversationsForUser() {
        final String userId = "userId";

        // Credentials
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(
                new Credential(91, userId, TINDER, "tinderCredential_91", "label 91"),
                new Credential(92, userId, HAPPN, "happnCredential_92", "label 92")));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getConversations("tinderCredential_91")).thenReturn(Flux.just(
                new ConversationDto("convTinder11", new ProfileDto("profTinder11", "profile name 11", 11, List.of("https://google.fr/favicon.png")), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11")),
                new ConversationDto("convTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.getConversations("happnCredential_92")).thenReturn(Flux.just(
                new ConversationDto("convHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21"))));
        providerServices.add(happnProviderService);

        Mono<PartialList<ConversationDto>> result = service.getConversationsForUser(userId);

        PartialList<ConversationDto> conversations = result.block();
        assertThat(conversations.isPartial(), is(false));
        assertThat(conversations, contains(
                new ConversationDto("91#convTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null),
                new ConversationDto("92#convHappn21", new ProfileDto("92#profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("92#messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21")),
                new ConversationDto("91#convTinder11", new ProfileDto("91#profTinder11", "profile name 11", 11, List.of(encode("https://google.fr/favicon.png"))), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("91#messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11"))));
    }

    @Test
    public void getConversationsForUser_partial() {
        final String userId = "userId";

        // Credentials
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(
                new Credential(91, userId, TINDER, "tinderCredential_91", "label 91"),
                new Credential(92, userId, HAPPN, "happnCredential_92", "label 92")));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getConversations("tinderCredential_91")).thenReturn(Flux.just(
                new ConversationDto("convTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.getConversations("happnCredential_92")).thenReturn(Flux.error(new ExpiredTokenException(null)));
        providerServices.add(happnProviderService);

        Mono<PartialList<ConversationDto>> result = service.getConversationsForUser(userId);

        PartialList<ConversationDto> conversations = result.block();
        assertThat(conversations.isPartial(), is(true));
        assertThat(conversations, contains(
                new ConversationDto("91#convTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
    }

    /**
     * Total execution for all providers = max(execution of provider)
     */
    @Test
    public void getConversationsForUser_parallel() {
        final String userId = "userId";
        final Duration delay = ofSeconds(5);

        StepVerifier.withVirtualTime(() -> {
            // Credentials
            when(credentialRepository.findByUserId(userId)).thenReturn(List.of(
                    new Credential(91, userId, TINDER, "tinderCredential_91", "label 91"),
                    new Credential(92, userId, HAPPN, "happnCredential_92", "label 92")));
            // Provider: TINDER
            ProviderService tinderProviderService = mock(ProviderService.class);
            when(tinderProviderService.getId()).thenReturn(TINDER);
            when(tinderProviderService.getConversations("tinderCredential_91")).thenReturn(
                    Mono.delay(delay)
                            .flatMapIterable(it -> List.of(new ConversationDto("convTinder11", new ProfileDto("profTinder11", "profile name 11", 11, emptyList()), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11")))));
            providerServices.add(tinderProviderService);
            // Provider: HAPPN
            ProviderService happnProviderService = mock(ProviderService.class);
            when(happnProviderService.getId()).thenReturn(HAPPN);
            when(happnProviderService.getConversations("happnCredential_92")).thenReturn(
                    Mono.delay(delay).flatMapIterable(it -> List.of(new ConversationDto("convHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21")))));
            providerServices.add(happnProviderService);

            return service.getConversationsForUser(userId);
        })
                .expectSubscription()
                .expectNoEvent(ofNanos((long) (delay.toNanos() * 0.8))) // waiting for provider response
                .thenAwait(ofNanos((long) (delay.toNanos() * 0.4))) // 120% (80+40) of delay < providerServices.size() * delay
                .expectNextMatches(it -> !it.isPartial() && it.size() == 2)
                .verifyComplete();
    }

    @Test
    public void getMessagesForUser() {
        final String userId = "userId";
        final int credentialId = 42;
        final String profileId = "99";

        // Credentials
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(new Credential(credentialId, userId, TINDER, "secret", "label")));
        // Provider
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getMessages("secret", profileId)).thenReturn(Flux.just(
                new MessageDto("mess2", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "text 2"),
                new MessageDto("mess3", ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), false, "text 3"),
                new MessageDto("mess1", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), false, "text 1")));
        providerServices.add(tinderProviderService);

        Flux<MessageDto> messages = service.getMessagesForUser(userId, credentialId, profileId);

        assertThat(messages.collectList().block(), contains(
                new MessageDto(credentialId + "#" + "mess1", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), false, "text 1"),
                new MessageDto(credentialId + "#" + "mess2", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "text 2"),
                new MessageDto(credentialId + "#" + "mess3", ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), false, "text 3")));
    }

    @Test
    public void getProfileForUser() {
        final String userId = "userId";
        final int credentialId = 42;
        final String profileId = "c11#99";

        // Credentials
        Credential credential = new Credential(credentialId, userId, TINDER, "secret", "label");
        when(credentialRepository.findByUserId(userId)).thenReturn(List.of(credential));
        // Provider
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getProfile("secret", profileId)).thenReturn(Mono.just(new ProfileDto("profTinder", "profile name", 1, List.of("https://google.fr/favicon.png"))));
        providerServices.add(tinderProviderService);

        Mono<ProfileDto> profile = service.getProfileForUser(userId, credentialId, profileId);

        assertThat(profile.block(), is(new ProfileDto(credentialId + "#" + "profTinder", "profile name", 1, List.of(encode("https://google.fr/favicon.png")))));
    }

}
