package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@Getter
public class TinderMatchDto {

    private final String id;
    private final List<TinderMessageDto> messages;
    private final TinderUserDto person;
    /**
     * Date of match or latest message.
     *
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime lastActivityDate;

    public TinderMatchDto(
            @JsonProperty(value = "_id", required = true) String id,
            @JsonProperty(value = "messages", required = true) List<TinderMessageDto> messages,
            @JsonProperty(value = "person", required = true) TinderUserDto person,
            @JsonProperty(value = "last_activity_date", required = true) ZonedDateTime lastActivityDate) {
        this.id = requireNonNull(id);
        this.messages = requireNonNull(messages);
        this.person = requireNonNull(person);
        this.lastActivityDate = requireNonNull(lastActivityDate);
    }
}
