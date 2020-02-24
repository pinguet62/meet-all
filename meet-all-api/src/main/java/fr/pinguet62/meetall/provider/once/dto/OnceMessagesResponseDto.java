package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMessagesResponseDto {

    private final OnceMessagesResultDto result;

    public OnceMessagesResponseDto(
            @JsonProperty(value = "result", required = true) OnceMessagesResultDto result) {
        this.result = requireNonNull(result);
    }
}
