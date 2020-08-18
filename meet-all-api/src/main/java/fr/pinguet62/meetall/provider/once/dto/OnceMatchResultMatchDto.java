package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMatchResultMatchDto {
    @NonNull
    String id;

    @NonNull
    OnceUserDto user;

    @NonNull
    boolean viewed;
}
