package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.Data;

@Data
public class HappnParticipantDto {

    @GraphQLField
    private HappnUserDto user;

}
