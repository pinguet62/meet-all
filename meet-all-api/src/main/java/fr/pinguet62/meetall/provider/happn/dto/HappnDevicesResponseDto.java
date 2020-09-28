package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Value
public class HappnDevicesResponseDto {
    /**
     * Can be {@code null} when never (or since a long time) connected from a device.
     */
    List<HappnDevicesDto> data;

    public Optional<List<HappnDevicesDto>> getData() {
        return ofNullable(data);
    }
}
