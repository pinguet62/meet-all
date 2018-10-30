package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

@Data
public class TinderMessageDto {
    private String _id;
    private String from;
    private String to;
    private String message;
    private String sent_date;
}
