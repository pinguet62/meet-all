package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGetConversationResponseDto {

    @Getter
    public static class TinderGetConversationDataResponseDto {

        private final List<TinderMatchDto> matches;

        public TinderGetConversationDataResponseDto(
                @JsonProperty(value = "matches", required = true) List<TinderMatchDto> matches) {
            this.matches = requireNonNull(matches);
        }
    }

    private final TinderGetConversationDataResponseDto data;

    public TinderGetConversationResponseDto(
            @JsonProperty(value = "data", required = true) TinderGetConversationDataResponseDto data) {
        this.data = requireNonNull(data);
    }
}
