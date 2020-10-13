package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMatchLikeResponseDto {
    @Value
    public static class OnceMatchLikeResultDto {
        @JsonProperty(required = true)
        boolean connected;
    }

    @NonNull
    OnceMatchLikeResultDto result;
}
