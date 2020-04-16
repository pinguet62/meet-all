package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class HappnOauthResponseDto {

    private final String accessToken;

    public HappnOauthResponseDto(
            @JsonProperty(value = "access_token", required = true) String accessToken) {
        this.accessToken = requireNonNull(accessToken);
    }

}
