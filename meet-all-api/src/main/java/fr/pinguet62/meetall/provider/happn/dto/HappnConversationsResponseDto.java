package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Data;

import java.util.List;

@Data
public class HappnConversationsResponseDto {

    private List<HappnConversationDto> data;

}
