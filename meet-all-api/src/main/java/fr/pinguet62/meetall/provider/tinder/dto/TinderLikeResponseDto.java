package fr.pinguet62.meetall.provider.tinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

@Getter
public class TinderLikeResponseDto {

    /**
     * <ul>
     * <li>Not matched: {@code false}</li>
     * <li>Matched: complex type</li>
     * </ul>
     */
    private final Object match;

    /***
     * When limit reached.
     */
    private final Long rateLimitedUntil;

    public TinderLikeResponseDto(
            @JsonProperty(value = "match", required = true) Object match,
            @JsonProperty("rate_limited_until") Long rateLimitedUntil) {
        this.match = requireNonNull(match);
        this.rateLimitedUntil = rateLimitedUntil;
    }

    public Optional<Long> getRateLimitedUntil() {
        return ofNullable(rateLimitedUntil);
    }

}
