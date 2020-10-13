package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class HappnOptionsDto {
    @JsonProperty(required = true)
    boolean success;

    @JsonProperty(required = true)
    int status;

    @NonNull
    Object data;
}
