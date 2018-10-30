package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

import java.util.List;

@Data
public class TinderGetConversationResponseDto {

    @Data
    public static class TinderGetConversationDataResponseDto {
        private List<TinderMatchDto> matches;
    }

    private TinderGetConversationDataResponseDto data;

}
