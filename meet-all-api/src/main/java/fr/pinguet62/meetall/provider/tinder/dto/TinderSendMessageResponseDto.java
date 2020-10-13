package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;
import lombok.Value;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Value
public class TinderSendMessageResponseDto {
    @NonNull
    String _id;

    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @NonNull
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    ZonedDateTime sent_date;

    @NonNull
    String message;
}
