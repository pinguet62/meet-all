package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceMessagesDto {
    @NonNull
    String id;

    @NonNull
    int number;

    /**
     * @see OnceUserDto#getId()
     */
    @NonNull
    String sender_id;

    @NonNull
    String message;

    @NonNull
    long created_at;
}
