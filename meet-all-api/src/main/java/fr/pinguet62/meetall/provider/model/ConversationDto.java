package fr.pinguet62.meetall.provider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;
import lombok.Value;
import lombok.With;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Optional.ofNullable;

@Value
public class ConversationDto {
    @Schema(example = "TINDER#0123456789",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NonNull
    @With
    @NotEmpty
    String id;

    @NonNull
    @With
    @NotNull
    @Valid
    ProfileDto profile; // TODO LazyProfileDto

    @Schema(required = true,
            example = "2018-10-26T10:09:42.830Z",
            description = "Date of match or latest message")
    @NonNull
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @NotNull
    ZonedDateTime date;

    @Nullable
    @With
    MessageDto lastMessage; // TODO LazyMessageDto

    public Optional<MessageDto> getLastMessage() {
        return ofNullable(lastMessage);
    }

}
