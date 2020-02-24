package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceSendMessageResponseDto {

    private final OnceMessagesDto result;

    public OnceSendMessageResponseDto(
            @JsonProperty(value = "result", required = true) OnceMessagesDto result) {
        this.result = requireNonNull(result);
    }

}
