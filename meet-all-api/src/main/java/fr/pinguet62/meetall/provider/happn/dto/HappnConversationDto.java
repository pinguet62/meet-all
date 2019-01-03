package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class HappnConversationDto {

    private String id;
    @GraphQLField
    private List<HappnParticipantDto> participants;
    @GraphQLField
    private List<HappnMessageDto> messages;
    /**
     * @example {@code 2018-11-26T10:05:52+00:00}
     */
    private OffsetDateTime modification_date;

}
