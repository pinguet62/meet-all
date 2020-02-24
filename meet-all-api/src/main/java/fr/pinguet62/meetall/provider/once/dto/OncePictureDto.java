package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OncePictureDto {

    /**
     * @example {@code "33485378_original.jpg"}
     */
    private final String original;

    public OncePictureDto(
            @JsonProperty(value = "original", required = true) String original) {
        this.original = requireNonNull(original);
    }
}
