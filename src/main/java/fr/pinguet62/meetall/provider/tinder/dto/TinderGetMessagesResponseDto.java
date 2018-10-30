package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

import java.util.List;

@Data
public class TinderGetMessagesResponseDto {

    @Data
    public static class TinderGetMessagesDataResponseDto {
        private List<TinderMessageDto> messages;
    }

    private TinderGetMessagesDataResponseDto data;

}
