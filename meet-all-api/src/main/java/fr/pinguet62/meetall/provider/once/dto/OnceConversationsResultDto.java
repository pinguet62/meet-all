package fr.pinguet62.meetall.provider.once.dto;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class OnceConversationsResultDto {
    @NonNull
    List<OnceConnectionDto> connections;

    @NonNull
    String base_url;
}
