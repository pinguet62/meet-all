package fr.pinguet62.meetall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@EqualsAndHashCode // testing
@ToString(callSuper = true) // testing
@Getter
public class ConversationDto {

    @NotEmpty
    private final String id;

    @NotNull
    @Valid
    private final ProfileDto profile; // TODO LazyProfileDto

    /**
     * Date of match or latest message.
     *
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime date;

    private final MessageDto lastMessage; // TODO LazyMessageDto

    public ConversationDto(String id, ProfileDto profile, ZonedDateTime date, MessageDto lastMessage) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
        this.date = requireNonNull(date);
        this.lastMessage = lastMessage;
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
