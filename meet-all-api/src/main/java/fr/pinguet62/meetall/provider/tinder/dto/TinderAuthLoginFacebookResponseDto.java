package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderAuthLoginFacebookResponseDto {
    @Value
    public static class TinderAuthLoginFacebookResponseDataDto {
        @NonNull
        String api_token;
    }

    @NonNull
    TinderAuthLoginFacebookResponseDataDto data;
}
