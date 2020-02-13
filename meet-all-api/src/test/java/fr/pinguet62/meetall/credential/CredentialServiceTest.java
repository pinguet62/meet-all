package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.provider.ProviderFactory;
import fr.pinguet62.meetall.provider.ProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CredentialServiceTest {

    CredentialRepository credentialRepository;
    ProviderFactory providerFactory;
    CredentialService service;

    @BeforeEach
    void buildService() {
        credentialRepository = mock(CredentialRepository.class);
        providerFactory = mock(ProviderFactory.class);
        service = new CredentialService(credentialRepository, providerFactory);
    }

    @Test
    void getRegisteredCredentials() {
        when(credentialRepository.findByUserId("userId")).thenReturn(Flux.just(
                new Credential("91", "userId", TINDER, "tinderCredential_91", "label 91"),
                new Credential("92", "userId", HAPPN, "happnCredential_92", "label 92")));
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
                new RegisteredCredentialDto("91", "label 91", TINDER, true),
                new RegisteredCredentialDto("92", "label 92", HAPPN, false)));
    }

    @Test
    void deleteCredential() {
        Credential tinderCredential = new Credential("99", "userId", TINDER, "credential_99", "label 99");
        when(credentialRepository.findById("99")).thenReturn(Mono.just(tinderCredential));
        when(credentialRepository.delete(tinderCredential)).thenReturn(Mono.empty());
        when(providerFactory.getProviderService(TINDER)).thenAnswer(a -> {
            ProviderService providerService = mock(ProviderService.class);
            when(providerService.checkCredential("credential_99")).thenReturn(Mono.just(true));
            return providerService;
        });

        assertThat(service.deleteCredential("userId", "99").block(), is(new RegisteredCredentialDto("99", "label 99", TINDER, true)));
    }

}
