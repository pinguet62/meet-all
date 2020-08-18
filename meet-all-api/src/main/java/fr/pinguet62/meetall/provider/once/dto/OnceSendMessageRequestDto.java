package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceSendMessageRequestDto {
    @NonNull
    String match_id;

    @NonNull
    String message;
}
