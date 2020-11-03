package fr.pinguet62.meetall.provider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Optional.ofNullable;

@Value
public class ConversationDto {
    @Value
    public static class LazyProfileDto {
        @Schema(example = "TINDER#azertyuiop",
                description = """
                        Internal ID<br>
                        Generated from provider's *key* and provider's *id*""")
        @NonNull
        @NotEmpty
        @With
        String id;

        @Schema(example = "Céline")
        @NonNull
        @NotEmpty
        String name;

        @ArraySchema(schema = @Schema(example = "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_150x54dp.png"))
        @With
        String avatar;

        public Optional<String> getAvatar() {
            return ofNullable(avatar);
        }
    }

    @Value
    public static class LazyMessageDto {
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

    @Schema(example = "TINDER#0123456789",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NonNull
    @NotEmpty
    @With
    String id;

    @NonNull
    @NotNull
    @Valid
    @With
    LazyProfileDto profile;

    @Schema(required = true,
            example = "2018-10-26T10:09:42.830Z",
            description = "Date of match or latest message")
    @NonNull
    @NotNull
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    ZonedDateTime date;

    @Valid
    @With
    LazyMessageDto lastMessage;

    public Optional<LazyMessageDto> getLastMessage() {
        return ofNullable(lastMessage);
    }
}
