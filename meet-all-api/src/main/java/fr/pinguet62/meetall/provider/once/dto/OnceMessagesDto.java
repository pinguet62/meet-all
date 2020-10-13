package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMessagesDto {
    @NonNull
    String id;

    @JsonProperty(required = true)
    int number;

    /**
     * @see OnceUserDto#getId()
     */
    @NonNull
    String sender_id;

    @NonNull
    String message;

    @JsonProperty(required = true)
    long created_at;
}
