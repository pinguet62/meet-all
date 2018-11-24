package fr.pinguet62.meetall.dto;

import lombok.Getter;

import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@Getter
public class ConversationDto {

    @NotNull
    private final String id;

    @NotNull
    private final ProfileDto profile; // TODO LazyProfileDto

    private final MessageDto lastMessage;

    public ConversationDto(String id, ProfileDto profile, MessageDto lastMessage) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
        this.lastMessage = lastMessage;
    }

}
