package fr.pinguet62.meetall.provider.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@ToString
@EqualsAndHashCode
@Getter
public class ConversationDto {

    @Schema(example = "TINDER#0123456789",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NotEmpty
    private final String id;

    @NotNull
    @Valid
    private final ProfileDto profile; // TODO LazyProfileDto

    @Schema(required = true,
            example = "2018-10-26T10:09:42.830Z",
            description = "Date of match or latest message")
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime date;

    @Nullable
    private final MessageDto lastMessage; // TODO LazyMessageDto

    public ConversationDto(String id, ProfileDto profile, ZonedDateTime date, @Nullable MessageDto lastMessage) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
        this.date = requireNonNull(date);
        this.lastMessage = lastMessage;
    }

    public Optional<MessageDto> getLastMessage() {
        return ofNullable(lastMessage);
    }

    public ConversationDto withId(String value) {
        if (value.equals(this.id)) return this;
        return new ConversationDto(value, this.profile, this.date, this.lastMessage);
    }

    public ConversationDto withProfile(ProfileDto value) {
        if (value.equals(this.profile)) return this;
        return new ConversationDto(this.id, value, this.date, this.lastMessage);
    }

    public ConversationDto withLastMessage(MessageDto value) {
        if (value.equals(this.lastMessage)) return this;
        return new ConversationDto(this.id, this.profile, this.date, value);
    }

}
