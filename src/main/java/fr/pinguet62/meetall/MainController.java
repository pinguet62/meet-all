package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.SecurityContext;
import fr.pinguet62.meetall.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
public class MainController {

    private final ProvidersService providersService;

    @GetMapping("/conversations")
    public Flux<ConversationDto> getConversations() {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .flatMapMany(providersService::getConversationsForUser);
    }

    @GetMapping("/messages")
    public Flux<MessageDto> getMessages(@RequestParam String providerAndId) {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .flatMapMany(userId -> providersService.getMessagesForUser(userId, ProviderIdValue.parse(providerAndId)));
    }

    @GetMapping("/profile")
    public Mono<ProfileDto> getProfile(@RequestParam String providerAndId) {
        return SecurityContextHolder.getContext().map(SecurityContext::getUserId)
                .flatMap(userId -> providersService.getProfileForUser(userId, ProviderIdValue.parse(providerAndId)));
    }

}
