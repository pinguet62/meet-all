package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HappnOauthRequestDto {

    @NonNull
    @JsonProperty("grant_type")
    private final String grantType;

    @NonNull
    @JsonProperty("client_id")
    private final String clientId;

    @NonNull
    @JsonProperty("client_secret")
    private final String clientSecret;

    @NonNull
    @JsonProperty("assertion_type")
    private final String assertionType;

    @NonNull
    @JsonProperty("assertion")
    private final String assertion;

}
