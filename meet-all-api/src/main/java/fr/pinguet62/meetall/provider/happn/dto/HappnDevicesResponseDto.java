package fr.pinguet62.meetall.provider.happn.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Value
public class HappnDevicesResponseDto {
    @Value
    public static class HappnDevicesDto {
        @NonNull
        String id;
    }

    /**
     * Can be {@code null} when never (or since a long time) connected from a device.
     */
    List<HappnDevicesDto> data;

    public Optional<List<HappnDevicesDto>> getData() {
        return ofNullable(data);
    }
}
