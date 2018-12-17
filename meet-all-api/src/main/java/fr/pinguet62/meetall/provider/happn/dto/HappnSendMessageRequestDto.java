package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class HappnSendMessageRequestDto {

    private final String message;

    public HappnSendMessageRequestDto(String message) {
        this.message = requireNonNull(message);
    }

}
