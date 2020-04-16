package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class OnceAuthenticateFacebookRequestDto {

    @NonNull
    @JsonProperty("facebook_token")
    private final String facebookToken;

}
