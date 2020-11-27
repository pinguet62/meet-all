package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NonNull;
import lombok.Value;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Optional.empty;
import static java.util.Optional.of;

@Value
public class TinderUserDto {
    @Value
    public static class TinderPhotoDto {
        @NonNull
        String url;
    }

    @NonNull
    String _id;

    /**
     * When undefined:
     * <ul>
     *     <li>From {@code "/matches"}: is absent from JSON</li>
     *     <li>From {@code "/user"}: is {@code ""}</li>
     * </ul>
     */
    // TODO @NonNull
    String bio;

    /**
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @NonNull
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    ZonedDateTime birth_date;

    @NonNull
    String name;

    @NonNull
    List<TinderPhotoDto> photos;

    public Optional<String> getBio() {
        return bio == null || bio.isEmpty() ? empty() : of(bio);
    }
}
