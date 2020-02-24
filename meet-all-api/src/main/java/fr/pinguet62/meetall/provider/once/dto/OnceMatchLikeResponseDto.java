package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchLikeResponseDto {

    private final OnceMatchLikeResultDto result;

    public OnceMatchLikeResponseDto(
            @JsonProperty(value = "result", required = true) OnceMatchLikeResultDto result) {
        this.result = requireNonNull(result);
    }
}
