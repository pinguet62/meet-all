package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceUserDto {

    private final String id;
    private final String first_name;
    private final Long age;
    private final List<OncePictureDto> pictures;

    public OnceUserDto(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "first_name", required = true) String first_name,
            @JsonProperty(value = "age", required = true) Long age,
            @JsonProperty(value = "pictures", required = true) List<OncePictureDto> pictures) {
        this.id = requireNonNull(id);
        this.first_name = requireNonNull(first_name);
        this.age = requireNonNull(age);
        this.pictures = requireNonNull(pictures);
    }

}
