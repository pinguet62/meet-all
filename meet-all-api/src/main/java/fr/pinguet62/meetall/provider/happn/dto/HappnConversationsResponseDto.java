package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.List;

@Value
public class HappnConversationsResponseDto {
    @Value
    public static class HappnConversationDto {
        @Value
        public static class HappnParticipantDto {
            @NonNull
            @GraphQLField
            HappnUserDto user;
        }

        @NonNull
        String id;

        @NonNull
        @GraphQLField
        List<HappnParticipantDto> participants;

        @NonNull
        @GraphQLField
        List<HappnMessageDto> messages;

        /**
         * @example {@code 2018-11-26T10:05:52+00:00}
         */
        @NonNull
        OffsetDateTime modification_date;
    }

    @NonNull
    List<HappnConversationDto> data;
}
