package fr.pinguet62.meetall.provider.happn.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class HappnDeviceResponseDto {
    @Value
    public static class HappnDeviceDto {
        @NonNull
        String id;
    }

    @NonNull
    HappnDeviceDto data;
}
