package fr.pinguet62.meetall.provider.model;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Value
public class ProfileDto {
    @Schema(example = "TINDER#azertyuiop",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NonNull
    @With
    @NotEmpty
    String id;

    @Schema(example = "Céline")
    @NonNull
    @NotEmpty
    String name;

    @Schema(required = true, minimum = "0", example = "27")
    @PositiveOrZero
    int age;

    @ArraySchema(schema = @Schema(example = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_150x54dp.png"))
    @NonNull
    @With
    @NotNull
    List<@NotEmpty String> avatars;
}
