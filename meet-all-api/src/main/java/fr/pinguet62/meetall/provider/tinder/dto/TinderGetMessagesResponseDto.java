package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class TinderGetMessagesResponseDto {
    @Value
    public static class TinderGetMessagesDataResponseDto {
        @NonNull
        List<TinderMessageDto> messages;
    }

    @NonNull
    TinderGetMessagesDataResponseDto data;
}
