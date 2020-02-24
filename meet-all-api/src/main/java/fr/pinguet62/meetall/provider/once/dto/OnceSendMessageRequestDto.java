package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceSendMessageRequestDto {

    @JsonProperty("match_id")
    private final String matchId;
    private final String message;

    public OnceSendMessageRequestDto(String matchId, String message) {
        this.matchId = requireNonNull(matchId);
        this.message = requireNonNull(message);
    }

}
