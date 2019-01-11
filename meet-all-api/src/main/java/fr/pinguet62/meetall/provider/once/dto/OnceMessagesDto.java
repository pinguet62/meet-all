package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

@Data
public class OnceMessagesDto {

    private String id;
    private Integer number;
    /**
     * @see OnceUserDto#getId()
     */
    private String sender_id;
    private String message;
    private Long created_at;

}
