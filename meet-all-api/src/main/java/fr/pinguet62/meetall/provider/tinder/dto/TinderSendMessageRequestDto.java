package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderSendMessageRequestDto {

    private final String message;

    public TinderSendMessageRequestDto(String message) {
        this.message = requireNonNull(message);
    }

}
