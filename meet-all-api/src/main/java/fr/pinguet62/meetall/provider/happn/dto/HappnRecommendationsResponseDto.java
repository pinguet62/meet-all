package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.pinguet62.meetall.provider.happn.GraphQLField;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnProfileDto;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Value
public class HappnRecommendationsResponseDto {
    @Value
    public static class HappnRecommendationDto {
        @Value
        public static class HappnRecommendationContentDto {
            @Value
            public static class HappnUserV1Dto {
                @NonNull
                String id;

                String first_name;

                String about;

                @JsonProperty(required = true)
                int age;

                @NonNull
                @GraphQLField(additional = ".mode(0).width(1000).height(1000)")
                List<HappnProfileDto> profiles;

                public Optional<String> getAbout() {
                    return about == null || about.isEmpty() ? empty() : of(about);
                }
            }

            @NonNull
            @GraphQLField
            HappnUserV1Dto user;
        }

        @NonNull
        @GraphQLField
        HappnRecommendationContentDto content;
    }

    @NonNull
    List<HappnRecommendationDto> data;
}
