package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderAuthLoginFacebookResponseDto {
    @NonNull
    TinderAuthLoginFacebookResponseDataDto data;
}
