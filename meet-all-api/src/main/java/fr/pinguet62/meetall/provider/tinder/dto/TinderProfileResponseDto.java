package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class TinderProfileResponseDto {
    @Value
    public static class TinderProfileDto {
        @Value
        public static class TinderProfileLikesDto {
            @JsonProperty(required = true)
            int likes_remaining;
        }

        /**
         * Require {@code "?include=likes"} query parameter.
         */
        @NonNull
        TinderProfileLikesDto likes;
    }

    @NonNull
    TinderProfileDto data;
}
