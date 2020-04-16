package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderAuthLoginFacebookResponseDataDto {

    private final String apiToken;

    public TinderAuthLoginFacebookResponseDataDto(
            @JsonProperty("api_token") String apiToken) {
        this.apiToken = requireNonNull(apiToken);
    }

}
