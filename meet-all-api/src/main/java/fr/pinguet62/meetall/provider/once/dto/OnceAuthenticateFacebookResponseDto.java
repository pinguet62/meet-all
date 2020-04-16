package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceAuthenticateFacebookResponseDto {

    private final String token;

    public OnceAuthenticateFacebookResponseDto(
            @JsonProperty(value = "token", required = true) String token) {
        this.token = requireNonNull(token);
    }

}
