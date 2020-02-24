package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@Getter
public class TinderMessageDto {

    private final String id;
    private final String from;
    private final String to;
    private final String message;
    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime sent_date;

    public TinderMessageDto(
            @JsonProperty(value = "_id", required = true) String id,
            @JsonProperty(value = "from", required = true) String from,
            @JsonProperty(value = "to", required = true) String to,
            @JsonProperty(value = "message", required = true) String message,
            @JsonProperty(value = "sent_date", required = true) ZonedDateTime sent_date) {
        this.id = requireNonNull(id);
        this.from = requireNonNull(from);
        this.to = requireNonNull(to);
        this.message = requireNonNull(message);
        this.sent_date = requireNonNull(sent_date);
    }

}
