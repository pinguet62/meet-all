package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.time.ZoneOffset.UTC;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProvidersServiceTest {

    private UserRepository userRepository;
    private List<ProviderService> providerServices;
    private ProvidersService service;

    @Before
    public void initMocks() {
        userRepository = mock(UserRepository.class);
        providerServices = new ArrayList<>();
        service = new ProvidersService(userRepository, providerServices);
    }

    @Test
    public void getProfileForUser() {
        final int userId = 3;
        final int credentialId = 42;
        final String profileId = "c11#99";

        // User: credentials
        User user = new User(userId, "email", "password");
        user.getProviderCredentials().add(new ProviderCredential(credentialId, user, TINDER, "secret", "label"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Provider
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getProfile("secret", profileId)).thenReturn(Mono.just(new ProfileDto("profTinder", "profile name", 1, emptyList())));
        providerServices.add(tinderProviderService);

        Mono<ProfileDto> profile = service.getProfileForUser(userId, credentialId, profileId);

        assertThat(profile.block(), is(new ProfileDto(credentialId + "#" + "profTinder", "profile name", 1, emptyList())));
    }

    @Test
    public void getConversationsForUser() {
        final int userId = 3;

        // User: credentials
        User user = new User(userId, "email", "password");
        user.getProviderCredentials().add(new ProviderCredential(81, user, TINDER, "tinderCredential_91", "label 11"));
        user.getProviderCredentials().add(new ProviderCredential(92, user, HAPPN, "happnCredential_92", "label 12"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.getConversations("tinderCredential_91")).thenReturn(Flux.just(
                new ConversationDto("convTinder11", new ProfileDto("profTinder11", "profile name 11", 11, emptyList()), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11")),
                new ConversationDto("convTinder12", new ProfileDto("profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null)));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.getConversations("happnCredential_92")).thenReturn(Flux.just(
                new ConversationDto("convHappn21", new ProfileDto("profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21"))));
        providerServices.add(happnProviderService);

        Flux<ConversationDto> conversations = service.getConversationsForUser(userId);

        assertThat(conversations.collectList().block(), contains(
                new ConversationDto("81#convTinder12", new ProfileDto("81#profTinder12", "profile name 12", 12, emptyList()), ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), null),
                new ConversationDto("92#convHappn21", new ProfileDto("92#profHappn21", "profile name 21", 21, emptyList()), ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), new MessageDto("92#messHappn21", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "message Happn 21")),
                new ConversationDto("81#convTinder11", new ProfileDto("81#profTinder11", "profile name 11", 11, emptyList()), ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), new MessageDto("81#messTinder11", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), true, "message Tinder 11"))));
    }

    @Test
    public void getMessagesForUser() {
        final int userId = 3;
        final int credentialId = 42;
        final String profileId = "99";

        // User: credentials
        User user = new User(userId, "email", "password");
        user.getProviderCredentials().add(new ProviderCredential(credentialId, user, TINDER, "secret", "label"));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
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
                new MessageDto(credentialId + "#" + "mess3", ZonedDateTime.of(2003, 8, 12, 5, 28, 56, 98, UTC), false, "text 3"),
                new MessageDto(credentialId + "#" + "mess2", ZonedDateTime.of(2002, 7, 9, 19, 52, 59, 12, UTC), false, "text 2"),
                new MessageDto(credentialId + "#" + "mess1", ZonedDateTime.of(2001, 4, 7, 9, 13, 37, 27, UTC), false, "text 1")));
    }

}
