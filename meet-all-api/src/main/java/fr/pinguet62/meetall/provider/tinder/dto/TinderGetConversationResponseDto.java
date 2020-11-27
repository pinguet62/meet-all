package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Value
public class TinderGetConversationResponseDto {
    @Value
    public static class TinderGetConversationDataResponseDto {
        @Value
        public static class TinderMatchDto {
            /**
             * {@code me} {@link TinderUserDto#_id} + {@link TinderMatchDto#person}.{@link TinderUserDto#_id}
             */
            @NonNull
            String _id;

            @NonNull
            List<TinderMessageDto> messages;

            @NonNull
            TinderUserDto person;

            /**
             * Date of match or latest message.
             *
             * @example {@code 2018-10-26T10:09:42.830Z}
             */
            @NonNull
            @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            ZonedDateTime last_activity_date;
        }

        @NonNull
        List<TinderMatchDto> matches;
    }

    @NonNull
    TinderGetConversationDataResponseDto data;
}
