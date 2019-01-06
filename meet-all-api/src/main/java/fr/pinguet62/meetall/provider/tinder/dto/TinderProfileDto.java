package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

@Data
public class TinderProfileDto {

    /**
     * Require {@code "?include=likes"} query parameter.
     */
    private TinderProfileLikesDto likes;

}
