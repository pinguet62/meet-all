package fr.pinguet62.meetall.provider;

import fr.pinguet62.meetall.ProviderIdValue;
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
    public ProviderService getProviderById(Provider provider) {
        Optional<ProviderService> first = providerServices.stream().filter(p -> p.getId().equals(provider)).findFirst();
        return first.orElseThrow(() -> new NoProviderFoundException(provider));
    }

    public Mono<ProfileDto> getProfileForUser(int userId, ProviderIdValue providerIdValue) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMap(user -> {
                    Provider provider = providerIdValue.getProvider();
                    ProviderCredential providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProvider().equals(provider)).findFirst().get();
                    ProviderService providerService = getProviderById(provider);
                    return providerService.getProfile(providerCredentials.getCredential(), providerIdValue.getValueId());
                });
    }

    /**
     * @see User#getId()
     */
    public Flux<ConversationDto> getConversationsForUser(int userId) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapIterable(User::getProviderCredentials)
                .flatMap(providerCredentials -> {
                    ProviderService providerService = getProviderById(providerCredentials.getProvider());
                    return providerService.getConversations(providerCredentials.getCredential());
                });
    }

    /**
     * @see User#getId()
     */
    public Flux<MessageDto> getMessagesForUser(int userId, ProviderIdValue providerIdValue) {
        return Mono.justOrEmpty(userRepository.findById(userId))
                .flatMapMany(user -> {
                    Provider provider = providerIdValue.getProvider();
                    ProviderCredential providerCredentials = user.getProviderCredentials().stream().filter(it -> it.getProvider().equals(provider)).findFirst().get();
                    ProviderService providerService = getProviderById(provider);
                    return providerService.getMessages(providerCredentials.getCredential(), providerIdValue.getValueId());
                });
    }

}
