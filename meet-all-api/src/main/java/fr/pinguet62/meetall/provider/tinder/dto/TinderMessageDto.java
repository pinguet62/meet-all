package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class TinderMessageDto {

    private String _id;
    private String from;
    private String to;
    private String message;
    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime sent_date;

}
