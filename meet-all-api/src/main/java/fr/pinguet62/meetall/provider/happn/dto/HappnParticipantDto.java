package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

@Value
public class HappnParticipantDto {
    @NonNull
    @GraphQLField
    HappnUserDto user;
}
