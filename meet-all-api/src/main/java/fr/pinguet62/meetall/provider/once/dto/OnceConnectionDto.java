package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OnceConnectionDto {
    @NonNull
    OnceUserDto user;

    /**
     * @see OnceUserDto#getId()
     */
    @NonNull
    String sender_id;

    @NonNull
    String match_id;

    /**
     * @see OnceMessagesDto#getMessage()
     */
    @NonNull
    String last_message;

    /**
     * @see OnceMessagesDto#getId()
     */
    @NonNull
    String last_message_id;

    /**
     * @see OnceMessagesDto#getCreatedAt()
     */
    @NonNull
    long message_sent_at;
}
