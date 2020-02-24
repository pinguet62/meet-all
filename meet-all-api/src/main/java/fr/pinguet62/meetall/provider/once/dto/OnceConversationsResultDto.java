package fr.pinguet62.meetall.provider.once.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class OnceConversationsResultDto {

    private final List<OnceConnectionDto> connections;
    private final String baseUrl;

    public OnceConversationsResultDto(
            @JsonProperty(value = "connections", required = true) List<OnceConnectionDto> connections,
            @JsonProperty(value = "base_url", required = true) String baseUrl) {
        this.connections = requireNonNull(connections);
        this.baseUrl = requireNonNull(baseUrl);
    }
}
