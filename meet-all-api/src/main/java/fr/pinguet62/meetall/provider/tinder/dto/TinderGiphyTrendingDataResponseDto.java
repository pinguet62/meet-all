package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGiphyTrendingDataResponseDto {

    private final String url;

    public TinderGiphyTrendingDataResponseDto(
            @JsonProperty("url") String url) {
        this.url = requireNonNull(url);
    }
}
