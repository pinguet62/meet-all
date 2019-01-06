package fr.pinguet62.meetall.provider.happn.dto;

import lombok.Data;

import java.util.List;

@Data
public class HappnNotificationsResponseDto {

    private List<HappnNotificationDto> data;

}
