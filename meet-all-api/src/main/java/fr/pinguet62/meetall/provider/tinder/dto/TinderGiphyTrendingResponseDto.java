package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class TinderGiphyTrendingResponseDto {
    @Value
    public static class TinderGiphyTrendingDataResponseDto {
        @NonNull
        String url;
    }

    @NonNull
    List<TinderGiphyTrendingDataResponseDto> data;
}
