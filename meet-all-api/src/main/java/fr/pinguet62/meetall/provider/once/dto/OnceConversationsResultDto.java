package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

import java.util.List;

@Data
public class OnceConversationsResultDto {

    private List<OnceConnectionDto> connections;
    private String base_url;

}
