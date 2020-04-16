package fr.pinguet62.meetall.facebookcredential;

import fr.pinguet62.meetall.credential.CredentialService;
import fr.pinguet62.meetall.credential.RegisteredCredentialDto;
import fr.pinguet62.meetall.provider.Provider;
import fr.pinguet62.meetall.provider.happn.HappnProviderService;
import fr.pinguet62.meetall.provider.once.OnceProviderService;
import fr.pinguet62.meetall.provider.tinder.TinderProviderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.validation.NoProviderFoundException;

@RequiredArgsConstructor
@Service
public class FacebookCredentialService {

    @NonNull
    private final RobotCredentialExtractor robotCredentialExtractor;
    @NonNull
    private final HappnProviderService happnProviderService;
    @NonNull
    private final OnceProviderService onceProviderService;
    @NonNull
    private final TinderProviderService tinderProviderService;
    @NonNull
    private final CredentialService credentialService;

    public Mono<RegisteredCredentialDto> register(String userId, Provider provider, String email, String password, String label) {
        return switch (provider) {
            case HAPPN -> robotCredentialExtractor.getHappnFacebookToken(email, password)
                    .flatMap(happnProviderService::loginWithFacebook)
                    .flatMap(credential -> credentialService.registerCredential(userId, provider, credential, label));
            case ONCE -> robotCredentialExtractor.getOnceFacebookToken(email, password)
                    .flatMap(onceProviderService::loginWithFacebook)
                    .flatMap(credential -> credentialService.registerCredential(userId, provider, credential, label));
            case TINDER -> robotCredentialExtractor.getTinderFacebookToken(email, password)
                    .flatMap(tinderProviderService::loginWithFacebook)
                    .flatMap(credential -> credentialService.registerCredential(userId, provider, credential, label));
            default -> throw new NoProviderFoundException("Unexpected value: " + provider);
        };
    }

}
