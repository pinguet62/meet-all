package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OnceMatchLikeResultDto {

    private final boolean connected;

    public OnceMatchLikeResultDto(
            @JsonProperty(value = "connected", required = true) boolean connected) {
        this.connected = connected;
    }
}
