package fr.pinguet62.meetall.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
public class ProfileDto {

    @NotEmpty
    private final String id;

    @NotEmpty
    private final String name;

    @PositiveOrZero
    private final int age;

    @NotNull
    private final List<@NotEmpty String> avatars;

    public ProfileDto(String id, String name, int age, List<String> avatars) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        if (age < 0) throw new IllegalArgumentException("'age' should be positive");
        this.age = age;
        this.avatars = requireNonNull(avatars);
    }

    public ProfileDto withId(String value) {
        if (value.equals(this.id)) return this;
        return new ProfileDto(value, this.name, this.age, this.avatars);
    }

    public ProfileDto withAvatars(List<String> value) {
        return new ProfileDto(this.id, this.name, this.age, value);
    }

}
