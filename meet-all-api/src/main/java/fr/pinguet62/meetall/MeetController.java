package fr.pinguet62.meetall;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.provider.ProvidersService;
import fr.pinguet62.meetall.security.ApplicationAuthentication;
import fr.pinguet62.meetall.security.ApplicationReactiveSecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;

@Tag(name = "Meet")
@RequiredArgsConstructor
@RestController
public class MeetController {

    private final ProvidersService providersService;

    @Operation(summary = "List all new (matchable) proposals")
    @GetMapping("/proposals")
    public Mono<ResponseEntity<List<ProposalDto>>> getProposals() {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(providersService::getProposalsForUser)
                .map(it -> it.isPartial() ? new ResponseEntity<>(it, PARTIAL_CONTENT) : ResponseEntity.ok(it));
    }

    @Operation(summary = "Like a proposal")
    @PostMapping("/proposals/{id}/like")
    public Mono<Boolean /*TODO Void*/> likeProposal(@PathVariable @Parameter(example = "TINDER#0123456789") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> providersService.likeOrUnlikeProposal(userId, transformedId.getCredentialId(), transformedId.getValueId(), true));
    }

    @Operation(summary = "Refuse a proposal")
    @PostMapping("/proposals/{id}/unlike")
    public Mono<Boolean /*TODO Void*/> unlikeProposal(@PathVariable @Parameter(example = "TINDER#0123456789") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> providersService.likeOrUnlikeProposal(userId, transformedId.getCredentialId(), transformedId.getValueId(), false));
    }

    @Operation(summary = "List all (available) conversations")
    @GetMapping("/conversations")
    public Mono<ResponseEntity<List<ConversationDto>>> getConversations() {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(providersService::getConversationsForUser)
                .map(it -> it.isPartial() ? new ResponseEntity<>(it, PARTIAL_CONTENT) : ResponseEntity.ok(it));
    }

    /**
     * @param id {@link TransformedId}
     */
    @GetMapping("/conversations/{id}/messages")
    public Flux<MessageDto> getMessages(@PathVariable @Parameter(example = "TINDER#0123456789") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMapMany(userId -> providersService.getMessagesForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @Operation(summary = "Send a message",
            responses = @ApiResponse(description = "The sent message" /*TODO sent: always true */ /*TODO text: equals to @Parameter */))
    @PostMapping("/conversations/{id}/message")
    @ResponseStatus(CREATED)
    public Mono<MessageDto> sendMessage(@PathVariable @Parameter(example = "TINDER#0123456789") String id, @RequestBody @Parameter(required = true, example = "Hello, how are you?") String text) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> providersService.sendMessage(userId, transformedId.getCredentialId(), transformedId.getValueId(), text));
    }

    /**
     * @param id {@link TransformedId}
     */
    @Operation(summary = "Get a profile")
    @GetMapping("/profile/{id}")
    public Mono<ProfileDto> getProfile(@PathVariable @Parameter(example = "TINDER#azertyuiop") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> providersService.getProfileForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

}
