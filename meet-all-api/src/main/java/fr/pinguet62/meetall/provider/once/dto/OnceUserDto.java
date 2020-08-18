package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceUserDto {
    @NonNull
    String id;

    @NonNull
    String first_name;

    @NonNull
    long age;

    @NonNull
    List<OncePictureDto> pictures;
}
