package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderAuthLoginFacebookResponseDto {

    private final TinderAuthLoginFacebookResponseDataDto data;

    public TinderAuthLoginFacebookResponseDto(
            @JsonProperty("data") TinderAuthLoginFacebookResponseDataDto data) {
        this.data = requireNonNull(data);
    }

}
