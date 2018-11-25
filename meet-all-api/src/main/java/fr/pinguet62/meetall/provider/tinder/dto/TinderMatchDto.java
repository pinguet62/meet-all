package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class TinderMatchDto {

    private String _id;
    private List<TinderMessageDto> messages;
    private TinderUserDto person;
    /**
     * Date of match or latest message.
     *
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime last_activity_date;

}
