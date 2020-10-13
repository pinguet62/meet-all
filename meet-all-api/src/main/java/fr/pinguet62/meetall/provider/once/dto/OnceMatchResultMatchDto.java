package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMatchResultMatchDto {
    @NonNull
    String id;

    @NonNull
    OnceUserDto user;

    @JsonProperty(required = true)
    boolean viewed;
}
