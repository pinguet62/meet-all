package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceMatchResultMatchDto {

    private final String id;
    private final OnceUserDto user;
    private final boolean viewed;

    public OnceMatchResultMatchDto(
            @JsonProperty(value = "id", required = true) String id,
            @JsonProperty(value = "user", required = true) OnceUserDto user,
            @JsonProperty(value = "viewed", required = true) boolean viewed) {
        this.id = requireNonNull(id);
        this.user = requireNonNull(user);
        this.viewed = viewed;
    }
}
