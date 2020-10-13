package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class HappnNotificationsResponseDto {
    @Value
    public static class HappnNotificationDto {
        @NonNull
        @GraphQLField
        HappnUserDto notifier;
    }

    @NonNull
    List<HappnNotificationDto> data;
}
