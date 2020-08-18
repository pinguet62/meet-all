package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderProfileLikesDto {
    @NonNull
    int likes_remaining;
}
