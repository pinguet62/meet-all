package fr.pinguet62.meetall.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

@Getter
public class MessageDto {

    @NotNull
    @NotEmpty
    private final String id;

    @NotNull
    @Past
    private final LocalDate date;

    @NotNull
    private final boolean sent;

    @NotNull
    @NotEmpty
    private final String text;

    public MessageDto(String id, LocalDate date, boolean sent, String text) {
        this.id = requireNonNull(id);
        this.date = requireNonNull(date);
        this.sent = sent;
        this.text = requireNonNull(text);
    }

}
