package fr.pinguet62.meetall.provider.once.dto;

import lombok.Data;

import java.util.List;

@Data
public class OnceMatchAllResultDto {

    private List<OnceMatchResultMatchDto> matches;
    private String base_url;

}
