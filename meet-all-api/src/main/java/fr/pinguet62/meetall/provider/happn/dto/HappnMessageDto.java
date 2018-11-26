package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HappnMessageDto {

    private String id;
    private String message;
    /**
     * @example {@code 2018-11-26T10:05:52+00:00}
     */
    private OffsetDateTime creation_date;
    private HappnUserDto sender;

}
