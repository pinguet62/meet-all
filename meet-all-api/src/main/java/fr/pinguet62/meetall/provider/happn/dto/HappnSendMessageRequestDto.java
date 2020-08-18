package fr.pinguet62.meetall.provider.happn.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class HappnSendMessageRequestDto {
    @NonNull
    String message;
}
