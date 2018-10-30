package fr.pinguet62.meetall.dto;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import static java.util.Objects.requireNonNull;

@Getter
public class ProfileDto {

    @NotNull
    @NotEmpty
    private final String id;

    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @PositiveOrZero
    private final int age;

    private final Object avatar;

    public ProfileDto(String id, String name, int age, Object avatar) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        if (age < 0) throw new IllegalArgumentException("'age' should be positive");
        this.age = age;
        this.avatar = avatar;
    }

    public ProfileDto(String id, String name, int age) {
        this(id, name, age, null);
    }

}
