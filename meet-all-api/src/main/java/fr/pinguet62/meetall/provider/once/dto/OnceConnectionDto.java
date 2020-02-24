package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceConnectionDto {

    private final OnceUserDto user;
    /**
     * @see OnceUserDto#getId()
     */
    private final String senderId;
    private final String matchId;
    /**
     * @see OnceMessagesDto#getMessage()
     */
    private final String lastMessage;
    /**
     * @see OnceMessagesDto#getId()
     */
    private final String lastMessageId;
    /**
     * @see OnceMessagesDto#getCreatedAt()
     */
    private final long messageSentAt;

    public OnceConnectionDto(
            @JsonProperty(value = "user", required = true) OnceUserDto user,
            @JsonProperty(value = "sender_id", required = true) String senderId,
            @JsonProperty(value = "match_id", required = true) String matchId,
            @JsonProperty(value = "last_message", required = true) String lastMessage,
            @JsonProperty(value = "last_message_id", required = true) String lastMessageId,
            @JsonProperty(value = "message_sent_at", required = true) long messageSentAt) {
        this.user = requireNonNull(user);
        this.senderId = requireNonNull(senderId);
        this.matchId = requireNonNull(matchId);
        this.lastMessage = requireNonNull(lastMessage);
        this.lastMessageId = requireNonNull(lastMessageId);
        this.messageSentAt = requireNonNull(messageSentAt);
    }
}
