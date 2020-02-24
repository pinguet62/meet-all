package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGetMetaResponseDto {

    private final TinderUserDto user;

    public TinderGetMetaResponseDto(
            @JsonProperty("user") TinderUserDto user) {
        this.user = requireNonNull(user);
    }
}
