package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@Getter
public class TinderUserDto {

    private final String id;
    private final String name;
    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime birth_date;
    private final List<TinderPhotoDto> photos;

    public TinderUserDto(
            @JsonProperty(value = "_id", required = true) String id,
            @JsonProperty(value = "name", required = true) String name,
            @JsonProperty(value = "birth_date", required = true) ZonedDateTime birth_date,
            @JsonProperty(value = "photos", required = true) List<TinderPhotoDto> photos) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.birth_date = requireNonNull(birth_date);
        this.photos = requireNonNull(photos);
    }

}
