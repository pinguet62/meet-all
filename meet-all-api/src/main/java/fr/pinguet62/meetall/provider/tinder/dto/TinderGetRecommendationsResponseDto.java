package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGetRecommendationsResponseDto {

    @Getter
    public static class TinderGetRecommendationsDataResponseDto {

        private final List<TinderRecommendationDto> results;

        public TinderGetRecommendationsDataResponseDto(
                @JsonProperty(value = "results", required = true) List<TinderRecommendationDto> results) {
            this.results = requireNonNull(results);
        }
    }

    @Getter
    public static class TinderRecommendationDto {

        private final TinderUserDto user;

        @JsonCreator
        public TinderRecommendationDto(
                @JsonProperty(value = "user", required = true) TinderUserDto user) {
            this.user = requireNonNull(user);
        }
    }

    private final TinderGetRecommendationsDataResponseDto data;

    public TinderGetRecommendationsResponseDto(
            @JsonProperty(value = "data", required = true) TinderGetRecommendationsDataResponseDto data) {
        this.data = requireNonNull(data);
    }
}


