package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchAllResultDto {

    private final List<OnceMatchResultMatchDto> matches;
    private final String baseUrl;

    public OnceMatchAllResultDto(
            @JsonProperty(value = "matches", required = true) List<OnceMatchResultMatchDto> matches,
            @JsonProperty(value = "base_url", required = true) String baseUrl) {
        this.matches = requireNonNull(matches);
        this.baseUrl = requireNonNull(baseUrl);
    }
}
