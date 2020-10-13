package fr.pinguet62.meetall.provider.happn.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class HappnUserAcceptedResponseDto {
    @Value
    public static class HappnUserAcceptedDataDto {
        // TODO refactor model: can be null
        // @JsonProperty(required = true)
        boolean has_crushed;
    }

    @NonNull
    HappnUserAcceptedDataDto data;
}
