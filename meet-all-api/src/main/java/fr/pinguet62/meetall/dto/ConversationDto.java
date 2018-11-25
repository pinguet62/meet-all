package fr.pinguet62.meetall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static java.util.Objects.requireNonNull;

@Getter
public class ConversationDto {

    @NotNull
    private final String id;

    @NotNull
    private final ProfileDto profile; // TODO LazyProfileDto

    /**
     * Date of match or latest message.
     *
     * @example {@code 2018-10-26T10:09:42.830Z}
     */
    @JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private final ZonedDateTime date;

    private final MessageDto lastMessage;

    public ConversationDto(String id, ProfileDto profile, ZonedDateTime date, MessageDto lastMessage) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
        this.date = requireNonNull(date);
        this.lastMessage = lastMessage;
    }

}
