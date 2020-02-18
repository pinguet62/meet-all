package fr.pinguet62.meetall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.ZonedDateTime;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
public class MessageDto {

    @Schema(example = "TINDER#mlkjhgfdsq",
            description = """
                    Internal ID<br>
                    Generated from provider's *key* and provider's *id*""")
    @NotEmpty
    private final String id;

    @Schema(example = "2018-10-26T10:09:42.830Z")
    @NotNull
    @Past
    private final ZonedDateTime date;

    @Schema(required = true, description = "If was sent, `false` otherwise.")
    private final boolean sent;

    @Schema(example = "Do you play football?")
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
