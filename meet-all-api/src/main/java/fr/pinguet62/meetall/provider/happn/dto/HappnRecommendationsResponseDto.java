package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class HappnRecommendationsResponseDto {
    @Value
    public static class HappnRecommendationDto {
        @Value
        public static class HappnRecommendationContentDto {
            @NonNull
            @GraphQLField
            HappnUserDto user;
        }

        @NonNull
        @GraphQLField
        HappnRecommendationContentDto content;
    }

    @NonNull
    List<HappnRecommendationDto> data;
}
