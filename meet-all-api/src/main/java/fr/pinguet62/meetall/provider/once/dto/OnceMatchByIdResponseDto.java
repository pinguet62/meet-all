package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchByIdResponseDto {

    private final OnceMatchByIdResultDto result;

    public OnceMatchByIdResponseDto(
            @JsonProperty(value = "result", required = true) OnceMatchByIdResultDto result) {
        this.result = requireNonNull(result);
    }
}
