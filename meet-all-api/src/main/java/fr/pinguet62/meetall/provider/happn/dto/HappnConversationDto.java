package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class HappnConversationDto {

    private String id;
    private List<HappnParticipantDto> participants;
    private List<HappnMessageDto> messages;
    /**
     * @example {@code 2018-11-26T10:05:52+00:00}
     */
    private OffsetDateTime modification_date;

}
