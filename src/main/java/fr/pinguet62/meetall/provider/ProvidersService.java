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

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProvidersService {

    private final UserRepository userRepository;

    private final List<ProviderService> providerServices;

    /**
     * @see ProviderService#getId()
     */
    public ProviderService getProviderServiceById(Provider provider) {
        Optional<ProviderService> first = providerServices.stream().filter(p -> p.getId().equals(provider)).findFirst();
        return first.orElseThrow(() -> new NoProviderFoundException(provider));
    }

    /**
     * @param currentUserId {@link User#getId()}
     * @param provider      {@link ProviderCredential#getProvider()}
     * @param profileId     {@link ProfileDto#getId()}
     */
    public Mono<ProfileDto> getProfileForUser(int currentUserId, Provider provider, String profileId) {
        return Mono.justOrEmpty(userRepository.findById(currentUserId))
                .flatMap(user -> {
                    ProviderCredential providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProvider().equals(provider)).findFirst().get();
                    ProviderService providerService = getProviderServiceById(provider);
                    return providerService.getProfile(providerCredentials.getCredential(), profileId);
                });
    }

    /**
     * @param currentUserId {@link User#getId()}
     */
    public Flux<ConversationDto> getConversationsForUser(int currentUserId) {
        return Mono.justOrEmpty(userRepository.findById(currentUserId))
                .flatMapIterable(User::getProviderCredentials)
                .flatMap(providerCredentials -> {
                    ProviderService providerService = getProviderServiceById(providerCredentials.getProvider());
                    return providerService.getConversations(providerCredentials.getCredential());
                });
    }

    /**
     * @param currentUserId {@link User#getId()}
     * @param provider      {@link ProviderCredential#getProvider()}
     * @param profileId     {@link ProfileDto#getId()}
     */
    public Flux<MessageDto> getMessagesForUser(int currentUserId, Provider provider, String profileId) {
        return Mono.justOrEmpty(userRepository.findById(currentUserId))
                .flatMapMany(user -> {
                    ProviderCredential providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProvider().equals(provider)).findFirst().get();
                    ProviderService providerService = getProviderServiceById(provider);
                    return providerService.getMessages(providerCredentials.getCredential(), profileId);
                });
    }

}
