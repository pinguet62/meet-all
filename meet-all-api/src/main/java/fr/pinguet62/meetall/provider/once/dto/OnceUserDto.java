package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceUserDto {
    @Value
    public static class OncePictureDto {
        /**
         * @example {@code "33485378_original.jpg"}
         */
        @NonNull
        String original;
    }

    @NonNull
    String id;

    @NonNull
    String first_name;

    @JsonProperty(required = true)
    long age;

    @NonNull
    List<OncePictureDto> pictures;
}
