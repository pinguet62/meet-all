package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class TinderProfileLikesDto {

    private final int likesRemaining;

    public TinderProfileLikesDto(
            @JsonProperty(value = "likes_remaining", required = true) int likesRemaining) {
        this.likesRemaining = likesRemaining;
    }

}
