package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@Getter
public class TinderSendMessageResponseDto {

    private final String id;
    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    private final ZonedDateTime sentDate;
    private final String message;

    public TinderSendMessageResponseDto(
            @JsonProperty(value = "_id", required = true) String id,
            @JsonProperty(value = "sent_date", required = true) @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime sentDate,
            @JsonProperty(value = "message", required = true) String message) {
        this.id = requireNonNull(id);
        this.sentDate = requireNonNull(sentDate);
        this.message = requireNonNull(message);
    }
}
