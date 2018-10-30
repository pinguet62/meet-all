package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MainController {

    @GetMapping("/conversations")
    public Flux<ConversationDto> getConversations() {
        return Flux.empty();
    }

    @GetMapping("/messages")
    public Flux<MessageDto> getMessages() {
        return Flux.empty();
    }

    @GetMapping("/profile")
    public Mono<ProfileDto> getProfile() {
        return Mono.empty();
    }

}
