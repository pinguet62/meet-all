package fr.pinguet62.meetall.provider.tinder.dto;

import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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

    /***
     * When limit reached.
     */
    @Nullable
    Long rate_limited_until;

    public Optional<Long> getRate_limited_until() {
        return ofNullable(rate_limited_until);
    }
}
