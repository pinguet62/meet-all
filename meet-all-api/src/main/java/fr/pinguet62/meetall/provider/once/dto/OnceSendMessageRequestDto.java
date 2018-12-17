package fr.pinguet62.meetall.provider.once.dto;

import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceSendMessageRequestDto {

    private final String match_id;
    private final String message;

    public OnceSendMessageRequestDto(String match_id, String message) {
        this.match_id = requireNonNull(match_id);
        this.message = requireNonNull(message);
    }

}
