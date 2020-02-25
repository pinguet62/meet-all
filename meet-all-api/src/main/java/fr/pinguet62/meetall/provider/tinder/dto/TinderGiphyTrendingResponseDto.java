package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGiphyTrendingResponseDto {

    private final List<TinderGiphyTrendingDataResponseDto> data;

    public TinderGiphyTrendingResponseDto(
            @JsonProperty("data") List<TinderGiphyTrendingDataResponseDto> data) {
        this.data = requireNonNull(data);
    }
}
