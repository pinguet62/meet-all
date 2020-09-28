package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.ExpiredTokenException;
import fr.pinguet62.meetall.PartialList;
import fr.pinguet62.meetall.credential.Credential;
import fr.pinguet62.meetall.credential.CredentialService;
import fr.pinguet62.meetall.provider.ProviderFactory;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;

import static fr.pinguet62.meetall.photoproxy.PhotoProxyEncoder.encode;
import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.time.Duration.ofNanos;
import static java.time.Duration.ofSeconds;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeetServiceTest {

    private CredentialService credentialService;
    private ProviderFactory providerFactory;
    private MeetService service;

    @Before
    public void initMocks() {
        credentialService = mock(CredentialService.class);
        providerFactory = mock(ProviderFactory.class);
        service = new MeetService(credentialService, providerFactory);
    }

    @Test
    public void getProposalsForUser() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getProposals("tinderCredential_91")).thenReturn(Flux.just(
                    new ProposalDto("propTinder11", new ProfileDto("profTinder11", "profile name 11", 11, List.of("https://google.fr/favicon.png"))),
                    new ProposalDto("propTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()))));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getProposals("happnCredential_92")).thenReturn(Flux.just(
                    new ProposalDto("propHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()))));
            return providerService;
        });

        Mono<PartialList<ProposalDto>> result = service.getProposalsForUser("userId");

        PartialList<ProposalDto> proposals = result.block();
        assertThat(proposals.isPartial(), is(false));
        assertThat(proposals, containsInAnyOrder(
                new ProposalDto("91#propTinder11", new ProfileDto("91#profTinder11", "profile name 11", 11, List.of(encode("https://google.fr/favicon.png")))),
                new ProposalDto("91#propTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList())),
                new ProposalDto("92#propHappn21", new ProfileDto("92#profHappn21", "profile name 21", 21, emptyList()))));
    }

    @Test
    public void getProposalsForUser_partial() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getProposals("tinderCredential_91")).thenReturn(Flux.just(
                    new ProposalDto("propTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()))));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getProposals("happnCredential_92")).thenReturn(Flux.error(new ExpiredTokenException(null)));
            return providerService;
        });

        Mono<PartialList<ProposalDto>> result = service.getProposalsForUser("userId");

        PartialList<ProposalDto> proposals = result.block();
        assertThat(proposals.isPartial(), is(true));
        assertThat(proposals, containsInAnyOrder(
                new ProposalDto("91#propTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList()))));
    }

    @Test
    public void getConversationsForUser() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getConversations("tinderCredential_91")).thenReturn(Flux.just(
                    new ConversationDto("convTinder11", new ProfileDto("profTinder11", "profile name 11", 11, List.of("https://google.fr/favicon.png")), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11")),
                    new ConversationDto("convTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getConversations("happnCredential_92")).thenReturn(Flux.just(
                    new ConversationDto("convHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21"))));
            return providerService;
        });

        Mono<PartialList<ConversationDto>> result = service.getConversationsForUser("userId");

        PartialList<ConversationDto> conversations = result.block();
        assertThat(conversations.isPartial(), is(false));
        assertThat(conversations, contains(
                new ConversationDto("91#convTinder12", new ProfileDto("91#profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null),
                new ConversationDto("92#convHappn21", new ProfileDto("92#profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("92#messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21")),
                new ConversationDto("91#convTinder11", new ProfileDto("91#profTinder11", "profile name 11", 11, List.of(encode("https://google.fr/favicon.png"))), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("91#messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11"))));
    }

    @Test
    public void getConversationsForUser_partial() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getConversations("tinderCredential_91")).thenReturn(Flux.just(
                    new ConversationDto("convTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getConversations("happnCredential_92")).thenReturn(Flux.error(new ExpiredTokenException(null)));
            return providerService;
        });

        Mono<PartialList<ConversationDto>> result = service.getConversationsForUser("userId");

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
        final Duration delay = ofSeconds(5);

        StepVerifier.withVirtualTime(() -> {
            when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                    new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                    new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
            when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
                ProviderService providerService = mock(ProviderService.class);
                when(providerService.getConversations("tinderCredential_91")).thenReturn(
                        Mono.delay(delay)
                                .flatMapIterable(it -> List.of(new ConversationDto("convTinder11", new ProfileDto("profTinder11", "profile name 11", 11, emptyList()), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11")))));
                return providerService;
            });
            when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
                ProviderService providerService = mock(ProviderService.class);
                when(providerService.getConversations("happnCredential_92")).thenReturn(
                        Mono.delay(delay).flatMapIterable(it -> List.of(new ConversationDto("convHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21")))));
                return providerService;
            });

            return service.getConversationsForUser("userId");
        })
                .expectSubscription()
                .expectNoEvent(ofNanos((long) (delay.toNanos() * 0.8))) // waiting for provider response
                .thenAwait(ofNanos((long) (delay.toNanos() * 0.4))) // 120% (80+40) of delay < providerServices.size() * delay
                .expectNextMatches(it -> !it.isPartial() && it.size() == 2)
                .verifyComplete();
    }

    @Test
    public void getMessagesForUser() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("42", "userId", TINDER, "secret", "label")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getMessages("secret", "99")).thenReturn(Flux.just(
                    new MessageDto("mess2", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "text 2"),
                    new MessageDto("mess3", ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), false, "text 3"),
                    new MessageDto("mess1", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), false, "text 1")));
            return providerService;
        });

        Flux<MessageDto> messages = service.getMessagesForUser("userId", "42", "99");

        assertThat(messages.collectList().block(), contains(
                new MessageDto("42#mess1", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), false, "text 1"),
                new MessageDto("42#mess2", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "text 2"),
                new MessageDto("42#mess3", ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), false, "text 3")));
    }

    @Test
    public void getProfileForUser() {
        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("42", "userId", TINDER, "secret", "label")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.getProfile("secret", "99")).thenReturn(Mono.just(new ProfileDto("profTinder", "profile name", 1, List.of("https://google.fr/favicon.png"))));
            return providerService;
        });

        Mono<ProfileDto> profile = service.getProfileForUser("userId", "42", "99");

        assertThat(profile.block(), is(new ProfileDto(42 + "#" + "profTinder", "profile name", 1, List.of(encode("https://google.fr/favicon.png")))));
    }

    @Test
    public void setPosition() {
        double latitude = 48.8534;
        double longitude = 2.3488;
        double altitude = 35.1;

        when(credentialService.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.setPosition("tinderCredential_91", latitude, longitude, altitude)).thenReturn(Mono.empty());
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.setPosition("happnCredential_92", latitude, longitude, altitude)).thenReturn(Mono.empty());
            return providerService;
        });

        StepVerifier.create(service.setPosition("userId", latitude, longitude, altitude))
                .verifyComplete();
    }

}
