package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.database.ProviderCredential;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.comparing;

@RequiredArgsConstructor
@Service
public class ProvidersService {

    private final UserRepository userRepository;

    private final List<ProviderService> providerServices;

    /**
     * @see ProviderService#getId()
     */
    public ProviderService getProviderService(Provider provider) {
        return providerServices.stream().filter(p -> p.getId().equals(provider)).findFirst()
                .orElseThrow(() -> new NoProviderFoundException(provider));
    }

    /**
     * @param userId    {@link User#getId()}
     * @param provider  {@link ProviderCredential#getProvider()}
     * @param profileId {@link ProfileDto#getId()}
     */
    public Mono<ProfileDto> getProfileForUser(int userId, Provider provider, String profileId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .filter(providerCredential -> providerCredential.getProvider().equals(provider))
                .next()
                .flatMap(providerCredential -> getProviderService(provider).getProfile(providerCredential.getCredential(), profileId));
    }

    /**
     * @param userId {@link User#getId()}
     */
    public Flux<ConversationDto> getConversationsForUser(int userId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .flatMap(providerCredential -> getProviderService(providerCredential.getProvider()).getConversations(providerCredential.getCredential()));
    }

    /**
     * @param userId    {@link User#getId()}
     * @param provider  {@link ProviderCredential#getProvider()}
     * @param profileId {@link ProfileDto#getId()}
     */
    public Flux<MessageDto> getMessagesForUser(int userId, Provider provider, String profileId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .filter(providerCredential -> providerCredential.getProvider().equals(provider))
                .next()
                .flatMapMany(providerCredential -> getProviderService(provider).getMessages(providerCredential.getCredential(), profileId));
    }

}
