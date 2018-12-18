package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.exception.UnauthorizedException;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.SecurityContext;
import fr.pinguet62.meetall.security.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;
import static reactor.core.publisher.Mono.error;

@RequiredArgsConstructor
@RestController
public class MeetController {

    private final ProvidersService providersService;

    @GetMapping("/conversations")
    public Mono<ResponseEntity<PartialList<ConversationDto>>> getConversations() {
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(providersService::getConversationsForUser)
                .map(it -> it.isPartial() ? new ResponseEntity<>(it, PARTIAL_CONTENT) : ResponseEntity.ok(it));
    }

    /**
     * @param id {@link TransformedId}
     */
    @GetMapping("/conversations/{id}/messages")
    public Flux<MessageDto> getMessages(@PathVariable String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMapMany(userId -> providersService.getMessagesForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @PostMapping("/conversations/{id}/message")
    @ResponseStatus(CREATED)
    public Mono<MessageDto> sendMessage(@PathVariable String id, @RequestBody String text) {
        TransformedId transformedId = TransformedId.parse(id);
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> providersService.sendMessage(userId, transformedId.getCredentialId(), transformedId.getValueId(), text));
    }

    /**
     * @param id {@link TransformedId}
     */
    @GetMapping("/profile/{id}")
    public Mono<ProfileDto> getProfile(@PathVariable String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return SecurityContextHolder.getContext()
                .switchIfEmpty(error(new UnauthorizedException()))
                .map(SecurityContext::getUserId)
                .flatMap(userId -> providersService.getProfileForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

}
