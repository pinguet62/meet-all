package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchByIdResultDto {

    private final OnceMatchResultMatchDto match;
    private final String baseUrl;

    public OnceMatchByIdResultDto(
            @JsonProperty(value = "match", required = true) OnceMatchResultMatchDto match,
            @JsonProperty(value = "base_url", required = true) String baseUrl) {
        this.match = requireNonNull(match);
        this.baseUrl = requireNonNull(baseUrl);
    }
}
