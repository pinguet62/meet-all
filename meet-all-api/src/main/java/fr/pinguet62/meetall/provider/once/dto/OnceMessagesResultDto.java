package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMessagesResultDto {

    private final OnceUserDto user;
    private final List<OnceMessagesDto> messages;

    public OnceMessagesResultDto(
            @JsonProperty(value = "user", required = true) OnceUserDto user,
            @JsonProperty(value = "messages", required = true) List<OnceMessagesDto> messages) {
        this.user = requireNonNull(user);
        this.messages = requireNonNull(messages);
    }
}
