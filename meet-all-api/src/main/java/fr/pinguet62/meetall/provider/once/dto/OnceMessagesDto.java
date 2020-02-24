package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMessagesDto {

    private final String id;
    private final Integer number;
    /**
     * @see OnceUserDto#getId()
     */
    private final String senderId;
    private final String message;
    private final Long createdAt;

    public OnceMessagesDto(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "number", required = true) Integer number,
            @JsonProperty(value = "sender_id", required = true) String senderId,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "created_at", required = true) Long createdAt) {
        this.id = requireNonNull(id);
        this.number = requireNonNull(number);
        this.senderId = requireNonNull(senderId);
        this.message = requireNonNull(message);
        this.createdAt = requireNonNull(createdAt);
    }
}
