package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderPhotoDto {

    private final String url;

    public TinderPhotoDto(
            @JsonProperty(value = "url", required = true) String url) {
        this.url = requireNonNull(url);
    }

}
