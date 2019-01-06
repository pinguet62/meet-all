package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

import java.util.List;

@Data
public class TinderGetRecommendationsResponseDto {

    @Data
    public static class TinderGetRecommendationsDataResponseDto {
        private List<TinderRecommendationDto> results;
    }

    private TinderGetRecommendationsDataResponseDto data;

}
