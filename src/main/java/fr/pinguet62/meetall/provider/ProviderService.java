package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.ProviderIdValue;
import fr.pinguet62.meetall.database.ProviderCredentials;
import fr.pinguet62.meetall.database.User;
import fr.pinguet62.meetall.database.UserRepository;
import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.tinder.TinderProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ProviderService {

    private final UserRepository userRepository;

    /**
     * @see Provider#getId()
     */
    public Provider getProviderById(String providerId) {
        // TODO Spring scan
        switch (providerId) {
            case "tinder":
                return new TinderProvider();
            default:
                throw new IllegalArgumentException("Unsupported provider: " + providerId);
        }
    }

    public Mono<ProfileDto> getProfileForUser(int userId, ProviderIdValue providerIdValue) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMap(user -> {
                    String providerId = providerIdValue.getProviderId();
                    ProviderCredentials providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProviderId().equals(providerId)).findFirst().get();
                    Provider provider = getProviderById(providerId);
                    return provider.getProfile(providerCredentials.getCredentials(), providerIdValue.getValueId());
                });
    }

    /**
     * @see fr.pinguet62.meetall.database.User#getId()
     */
    public Flux<ConversationDto> getConversationsForUser(int userId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .flatMap(providerCredentials -> {
                    Provider provider = getProviderById(providerCredentials.getProviderId());
                    return provider.getConversations(providerCredentials.getCredentials());
                });
    }

    /**
     * @see fr.pinguet62.meetall.database.User#getId()
     */
    public Flux<MessageDto> getMessagesForUser(int userId, ProviderIdValue providerIdValue) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapMany(user -> {
                    String providerId = providerIdValue.getProviderId();
                    ProviderCredentials providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProviderId().equals(providerId)).findFirst().get();
                    Provider provider = getProviderById(providerId);
                    return provider.getMessages(providerCredentials.getCredentials(), providerIdValue.getValueId());
                });
    }

}
