package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchAllResponseDto {

    private final OnceMatchAllResultDto result;

    public OnceMatchAllResponseDto(
            @JsonProperty(value = "result", required = true) OnceMatchAllResultDto result) {
        this.result = requireNonNull(result);
    }
}
