package fr.pinguet62.meetall.provider.happn.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class HappnConversationsResponseDto {
    @NonNull
    List<HappnConversationDto> data;
}
