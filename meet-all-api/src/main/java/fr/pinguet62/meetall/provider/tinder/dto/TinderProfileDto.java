package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderProfileDto {

    /**
     * Require {@code "?include=likes"} query parameter.
     */
    private final TinderProfileLikesDto likes;

    public TinderProfileDto(
            @JsonProperty(value = "likes", required = true) TinderProfileLikesDto likes) {
        this.likes = requireNonNull(likes);
    }
}
