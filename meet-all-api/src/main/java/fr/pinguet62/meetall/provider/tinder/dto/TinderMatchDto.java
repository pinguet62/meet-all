package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

import java.util.List;

@Data
public class TinderMatchDto {

    private String _id;
    private List<TinderMessageDto> messages;
    private TinderUserDto person;

}
