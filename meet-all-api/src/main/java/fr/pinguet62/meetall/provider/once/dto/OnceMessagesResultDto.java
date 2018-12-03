package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

import java.util.List;

@Data
public class OnceMessagesResultDto {

    private OnceUserDto user;
    private List<OnceMessagesDto> messages;

}
