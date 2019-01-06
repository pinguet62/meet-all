package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

@Data
public class OnceMatchByIdResultDto {

    private OnceMatchResultMatchDto match;
    private String base_url;

}
