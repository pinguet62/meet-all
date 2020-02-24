package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGetUserResponseDto {

    private final TinderUserDto results;

    public TinderGetUserResponseDto(
            @JsonProperty(value = "results", required = true) TinderUserDto results) {
        this.results = requireNonNull(results);
    }
}
