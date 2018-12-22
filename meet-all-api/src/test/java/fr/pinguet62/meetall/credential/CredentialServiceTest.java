package fr.pinguet62.meetall.credential;

import fr.pinguet62.meetall.dto.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.ProviderService;
import fr.pinguet62.meetall.provider.ProvidersService;
import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fr.pinguet62.meetall.provider.Provider.HAPPN;
import static fr.pinguet62.meetall.provider.Provider.TINDER;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CredentialServiceTest {

    private CredentialRepository credentialRepository;
    private List<ProviderService> providerServices;
    private ProvidersService providersService;
    private CredentialService service;

    @Before
    public void buildService() {
        credentialRepository = mock(CredentialRepository.class);
        providerServices = new ArrayList<>();
        providersService = new ProvidersService(credentialRepository, providerServices);
        service = new CredentialService(credentialRepository, providersService);
    }

    @Before
    public void initMockBehavior() {
        final String userId = "userId";

        // Credentials
        Credential credential91 = new Credential(91, userId, TINDER, "tinderCredential_91", "label 91");
        when(credentialRepository.findById(91)).thenReturn(Optional.of(credential91));
        Credential credential92 = new Credential(92, userId, HAPPN, "happnCredential_92", "label 92");
        when(credentialRepository.findById(92)).thenReturn(Optional.of(credential92));
        when(credentialRepository.findByUserId(userId)).thenReturn(asList(credential91, credential92));
        // Provider: TINDER
        ProviderService tinderProviderService = mock(ProviderService.class);
        when(tinderProviderService.getId()).thenReturn(TINDER);
        when(tinderProviderService.checkCredential("tinderCredential_91")).thenReturn(Mono.just(true));
        providerServices.add(tinderProviderService);
        // Provider: HAPPN
        ProviderService happnProviderService = mock(ProviderService.class);
        when(happnProviderService.getId()).thenReturn(HAPPN);
        when(happnProviderService.checkCredential("happnCredential_92")).thenReturn(Mono.just(false));
        providerServices.add(happnProviderService);
    }

    @Test
    public void getRegisteredCredentials() {
        final String userId = "userId";

        Flux<RegisteredCredentialDto> registeredCredentials = service.getRegisteredCredentials(userId);

        assertThat(registeredCredentials.collectList().block(), contains(
                new RegisteredCredentialDto(91, "label 91", TINDER, true),
                new RegisteredCredentialDto(92, "label 92", HAPPN, false)));
    }

    @Test
    public void deleteCredential() {
        final String userId = "userId";

        assertThat(service.deleteCredential(userId, 91).block(), is(new RegisteredCredentialDto(91, "label 91", TINDER, true)));
        assertThat(service.deleteCredential(userId, 92).block(), is(new RegisteredCredentialDto(92, "label 92", HAPPN, false)));
    }

}
