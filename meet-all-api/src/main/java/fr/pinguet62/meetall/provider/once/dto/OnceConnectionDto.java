package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

@Data
public class OnceConnectionDto {

    private OnceUserDto user;
    /**
     * @see OnceUserDto#getId()
     */
    private String sender_id;
    private String match_id;
    /**
     * @see OnceMessagesDto#getMessage()
     */
    private String last_message;
    /**
     * @see OnceMessagesDto#getId()
     */
    private String last_message_id;
    /**
     * @see OnceMessagesDto#getCreated_at()
     */
    private Long message_sent_at;

}
