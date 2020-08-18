package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMatchByIdResultDto {
    @NonNull
    OnceMatchResultMatchDto match;

    @NonNull
    String base_url;
}
