package fr.pinguet62.meetall.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

@EqualsAndHashCode // testing
@ToString(callSuper = true) // testing
@Getter
public class MessageDto {

    @NotNull
    @NotEmpty
    private final String id;

    @NotNull
    @Past
    private final ZonedDateTime date;

    @NotNull
    private final boolean sent;

    @NotNull
    @NotEmpty
    private final String text;

    public MessageDto(String id, ZonedDateTime date, boolean sent, String text) {
        this.id = requireNonNull(id);
        this.date = requireNonNull(date);
        this.sent = sent;
        this.text = requireNonNull(text);
    }

    public MessageDto withId(String value) {
        if (value.equals(this.id)) return this;
        return new MessageDto(value, this.date, this.sent, this.text);
    }

}
