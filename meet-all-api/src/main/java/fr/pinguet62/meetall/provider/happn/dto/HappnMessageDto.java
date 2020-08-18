package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Value
public class HappnMessageDto {
    @NonNull
    String id;

    @NonNull
    String message;

    /**
     * @example {@code 2018-11-26T10:05:52+00:00}
     */
    @NonNull
    OffsetDateTime creation_date;

    @Nullable
    @GraphQLField
    HappnUserDto sender;

    public Optional<HappnUserDto> getSender() {
        return ofNullable(sender);
    }
}
