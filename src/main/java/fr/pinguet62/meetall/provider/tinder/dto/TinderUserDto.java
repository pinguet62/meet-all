package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
public class TinderUserDto {

    private String _id;
    private String name;
    /**
     * @example {@code 1989-xx-xxTxx:xx:xx.xxxZ}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime birth_date;
    private List<TinderPhotoDto> photos;

}
