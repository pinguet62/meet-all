package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceMatchAllResponseDto {
    @Value
    public static class OnceMatchAllResultDto {
        @NonNull
        List<OnceMatchResultMatchDto> matches;

        @NonNull
        String base_url;
    }

    @NonNull
    OnceMatchAllResultDto result;
}
