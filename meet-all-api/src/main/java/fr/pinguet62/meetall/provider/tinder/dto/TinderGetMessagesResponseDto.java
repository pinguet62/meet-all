package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class TinderGetMessagesResponseDto {

    @Getter
    public static class TinderGetMessagesDataResponseDto {

        private final List<TinderMessageDto> messages;

        public TinderGetMessagesDataResponseDto(
                @JsonProperty(value = "messages", required = true) List<TinderMessageDto> messages) {
            this.messages = requireNonNull(messages);
        }
    }

    private final TinderGetMessagesDataResponseDto data;

    public TinderGetMessagesResponseDto(
            @JsonProperty(value = "data", required = true) TinderGetMessagesDataResponseDto data) {
        this.data = requireNonNull(data);
    }
}
