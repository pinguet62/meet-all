package fr.pinguet62.meetall.meet;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.security.ApplicationAuthentication;
import fr.pinguet62.meetall.security.ApplicationReactiveSecurityContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static fr.pinguet62.meetall.config.OpenApiConfig.BEARER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;

@Tag(name = "Meet")
@SecurityRequirement(name = BEARER)
@RequiredArgsConstructor
@RestController
class MeetController {

    private final MeetService meetService;

    @Operation(summary = "List all new (matchable) proposals")
    @GetMapping("/proposals")
    public Mono<ResponseEntity<List<ProposalDto>>> getProposals() {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(meetService::getProposalsForUser)
                .map(it -> it.isPartial() ? new ResponseEntity<>(it, PARTIAL_CONTENT) : ResponseEntity.ok(it));
    }

    @Operation(summary = "Refuse a proposal")
    @PostMapping("/proposals/{id}/pass")
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> passProposal(@PathVariable @Parameter(example = "TINDER#0123456789") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> meetService.passProposal(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @Operation(summary = "Like a proposal",
            responses = @ApiResponse(
                    description = "If the acceptation triggered a respective match",
                    content = @Content(schema = @Schema(type = "boolean", example = "true"))))
    @PostMapping("/proposals/{id}/like")
    public Mono<Boolean> likeProposal(@PathVariable @Parameter(example = "TINDER#0123456789") String id) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> meetService.likeProposal(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @Operation(summary = "List all (available) conversations")
    @GetMapping("/conversations")
    public Mono<ResponseEntity<List<ConversationDto>>> getConversations() {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(meetService::getConversationsForUser)
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
                .flatMapMany(userId -> meetService.getMessagesForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @Operation(summary = "Send a message",
            responses = @ApiResponse(description = "The sent message" /*TODO sent: always true */ /*TODO text: equals to @Parameter */))
    @PostMapping("/conversations/{id}/message")
    @ResponseStatus(CREATED)
    public Mono<MessageDto> sendMessage(@PathVariable @Parameter(example = "TINDER#0123456789") String id, @RequestBody @Parameter(required = true, example = "Hello, how are you?") String text) {
        TransformedId transformedId = TransformedId.parse(id);
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> meetService.sendMessage(userId, transformedId.getCredentialId(), transformedId.getValueId(), text));
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
                .flatMap(userId -> meetService.getProfileForUser(userId, transformedId.getCredentialId(), transformedId.getValueId()));
    }

    @PostMapping("/position")
    @ResponseStatus(NO_CONTENT)
    public Mono<Void> setPosition(
            @Parameter(example = "48.8534") @RequestParam float latitude,
            @Parameter(example = "2.3488") @RequestParam float longitude,
            @Parameter(example = "35.1") @RequestParam float altitude) {
        return ApplicationReactiveSecurityContextHolder.getAuthentication()
                .map(ApplicationAuthentication::getUserId)
                .flatMap(userId -> meetService.setPosition(userId, latitude, longitude, altitude));
    }
}
