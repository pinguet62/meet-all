package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderProfileResponseDto {

    private final TinderProfileDto data;

    public TinderProfileResponseDto(
            @JsonProperty(value = "data", required = true) TinderProfileDto data) {
        this.data = requireNonNull(data);
    }
}
