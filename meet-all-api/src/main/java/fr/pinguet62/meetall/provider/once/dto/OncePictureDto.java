package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class OncePictureDto {
    /**
     * @example {@code "33485378_original.jpg"}
     */
    @NonNull
    String original;
}
