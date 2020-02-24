package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceConversationsResponseDto {

    private final OnceConversationsResultDto result;

    public OnceConversationsResponseDto(
            @JsonProperty(value = "result", required = true) OnceConversationsResultDto result) {
        this.result = requireNonNull(result);
    }
}
