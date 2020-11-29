package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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
         * Default message: {@code null} or {@code "Vous avez été connectés"}.
         *
         * @see OnceMessagesDto#getMessage()
         */
        String last_message;

        /**
         * {@code 0} when {@link #last_message} is default message.
         */
        @JsonProperty(required = true)
        int last_message_id;

        /**
         * @see OnceMessagesDto#getCreated_at()
         */
        @JsonProperty(required = true)
        long message_sent_at;

        public Optional<String> getLast_message() {
            return ofNullable(last_message);
        }
    }

    @NonNull
    List<OnceConnectionDto> connections;

    @NonNull
    String base_url;
}
