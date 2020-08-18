package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class TinderGetConversationResponseDto {

    @Value
    public static class TinderGetConversationDataResponseDto {
        @NonNull
        List<TinderMatchDto> matches;
    }

    @NonNull
    TinderGetConversationDataResponseDto data;
}
