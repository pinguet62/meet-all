package fr.pinguet62.meetall;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.ProviderCredentialRepository;
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

    private ProviderCredentialRepository providerCredentialRepository;
    private List<ProviderService> providerServices;
    private ProvidersService providersService;
    private CredentialService service;

    @Before
    public void buildService() {
        providerCredentialRepository = mock(ProviderCredentialRepository.class);
        providerServices = new ArrayList<>();
        providersService = new ProvidersService(providerCredentialRepository, providerServices);
        service = new CredentialService(providerCredentialRepository, providersService);
    }

    @Before
    public void initMockBehavior() {
        final String userId = "userId";

        // Credentials
        ProviderCredential providerCredential91 = new ProviderCredential(91, userId, TINDER, "tinderCredential_91", "label 91");
        when(providerCredentialRepository.findById(91)).thenReturn(Optional.of(providerCredential91));
        ProviderCredential providerCredential92 = new ProviderCredential(92, userId, HAPPN, "happnCredential_92", "label 92");
        when(providerCredentialRepository.findById(92)).thenReturn(Optional.of(providerCredential92));
        when(providerCredentialRepository.findByUserId(userId)).thenReturn(asList(providerCredential91, providerCredential92));
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
