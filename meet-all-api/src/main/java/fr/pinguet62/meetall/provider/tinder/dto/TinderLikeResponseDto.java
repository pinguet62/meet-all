package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

@Value
public class TinderLikeResponseDto {
    /**
     * <ul>
     * <li>Not matched: {@code false}</li>
     * <li>Matched: complex type</li>
     * </ul>
     */
    @NonNull
    Object match;
}
