package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceAuthenticateFacebookResponseDto {
    @NonNull
    String token;
}
