package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.Data;

@Data
public class TinderLikeResponseDto {

    /**
     * <ul>
     * <li>Not matched: {@code false}</li>
     * <li>Matched: complex type</li>
     * </ul>
     */
    private Object match;

    /***
     * When limit reached.
     */
    private Long rate_limited_until;

}
