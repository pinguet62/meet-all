package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderAuthLoginFacebookResponseDataDto {
    @NonNull
    String api_token;
}
