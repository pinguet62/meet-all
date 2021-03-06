package fr.pinguet62.meetall.provider.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.ZonedDateTime;

@Value
public class MessageDto {
    @Schema(example = "TINDER#mlkjhgfdsq",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NonNull
    @NotEmpty
    @With
    String id;

    @Schema(example = "2018-10-26T10:09:42.830Z")
    @NonNull
    @NotNull
    @Past
    ZonedDateTime date;

    @Schema(required = true, description = "If was sent, otherwise `false`")
    boolean sent;

    @Schema(example = "Do you play football?")
    @NonNull
    @NotEmpty
    String text;
}
