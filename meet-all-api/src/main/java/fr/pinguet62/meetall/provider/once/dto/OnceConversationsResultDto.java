package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceConversationsResultDto {
    @Value
    public static class OnceConnectionDto {
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
        @JsonProperty(required = true)
        long message_sent_at;
    }

    @NonNull
    List<OnceConnectionDto> connections;

    @NonNull
    String base_url;
}
