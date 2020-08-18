package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class TinderGetRecommendationsResponseDto {

    @Value
    public static class TinderGetRecommendationsDataResponseDto {
        @NonNull
        List<TinderRecommendationDto> results;
    }

    @Value
    public static class TinderRecommendationDto {
        @NonNull
        TinderUserDto user;
    }

    @NonNull
    TinderGetRecommendationsDataResponseDto data;
}
