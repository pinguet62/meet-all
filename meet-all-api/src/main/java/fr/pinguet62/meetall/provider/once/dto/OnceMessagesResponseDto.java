package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceMessagesResponseDto {
    @Value
    public static class OnceMessagesResultDto {
        @NonNull
        OnceUserDto user;

        @NonNull
        List<OnceMessagesDto> messages;
    }

    @NonNull
    OnceMessagesResultDto result;
}
