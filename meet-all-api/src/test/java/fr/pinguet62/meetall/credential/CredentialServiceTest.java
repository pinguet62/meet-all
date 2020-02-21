package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.ProviderFactory;
import fr.pinguet62.meetall.provider.ProviderService;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CredentialServiceTest {

    private CredentialRepository credentialRepository;
    private ProviderFactory providerFactory;
    private CredentialService service;

    @Before
    public void buildService() {
        credentialRepository = mock(CredentialRepository.class);
        providerFactory = mock(ProviderFactory.class);
        service = new CredentialService(credentialRepository, providerFactory);
    }

    @Test
    public void getRegisteredCredentials() {
        when(credentialRepository.findByUserId("userId")).thenReturn(List.of(
                new Credential(91, "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential(92, "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.checkCredential("tinderCredential_91")).thenReturn(Mono.just(true));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.checkCredential("happnCredential_92")).thenReturn(Mono.just(false));
            return providerService;
        });

        Flux<RegisteredCredentialDto> registeredCredentials = service.getRegisteredCredentials("userId");

        assertThat(registeredCredentials.collectList().block(), contains(
                new RegisteredCredentialDto(91, "label 91", TINDER, true),
                new RegisteredCredentialDto(92, "label 92", HAPPN, false)));
    }

    @Test
    public void deleteCredential() {
        when(credentialRepository.findById(91)).thenReturn(Optional.of(new Credential(91, "userId", TINDER, "tinderCredential_91", "label 91")));
        when(credentialRepository.findById(92)).thenReturn(Optional.of(new Credential(92, "userId", HAPPN, "happnCredential_92", "label 92")));
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.checkCredential("tinderCredential_91")).thenReturn(Mono.just(true));
            return providerService;
        });
        when(providerFactory.getProviderService(HAPPN)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.checkCredential("happnCredential_92")).thenReturn(Mono.just(false));
            return providerService;
        });

        assertThat(service.deleteCredential("userId", 91).block(), is(new RegisteredCredentialDto(91, "label 91", TINDER, true)));
        assertThat(service.deleteCredential("userId", 92).block(), is(new RegisteredCredentialDto(92, "label 92", HAPPN, false)));
    }

}
