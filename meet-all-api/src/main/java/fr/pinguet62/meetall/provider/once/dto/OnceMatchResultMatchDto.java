package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

@Data
public class OnceMatchResultMatchDto {

    private String id;
    private OnceUserDto user;
    private Boolean viewed;

}
