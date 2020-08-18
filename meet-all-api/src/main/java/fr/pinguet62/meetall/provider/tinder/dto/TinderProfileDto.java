package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderProfileDto {
    /**
     * Require {@code "?include=likes"} query parameter.
     */
    @NonNull
    TinderProfileLikesDto likes;
}
