package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.exception.UnauthorizedException;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.SecurityContext;
import fr.pinguet62.meetall.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.error;

@RequiredArgsConstructor
@RestController
public class MeetController {

    private final ProvidersService providersService;

    @GetMapping("/conversations")
    public Flux<ConversationDto> getConversations() {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMapMany(providersService::getConversationsForUser);
    }

    /**
     * @param id {@link ProviderIdValue}
     */
    @GetMapping("/conversations/{id}/messages")
    public Flux<MessageDto> getMessages(@PathVariable String id) {
        ProviderIdValue providerIdValue = ProviderIdValue.parse(id);
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMapMany(userId -> providersService.getMessagesForUser(userId, providerIdValue.getProvider(), providerIdValue.getValueId()));
    }

    /**
     * @param id {@link ProviderIdValue}
     */
    @GetMapping("/profile/{id}")
    public Mono<ProfileDto> getProfile(@PathVariable String id) {
        ProviderIdValue providerIdValue = ProviderIdValue.parse(id);
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> providersService.getProfileForUser(userId, providerIdValue.getProvider(), providerIdValue.getValueId()));
    }

}
