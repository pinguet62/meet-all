package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.provider.happn.dto.HappnConversationsResponseDto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationsResponseDto.HappnNotificationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto.HappnRecommendationContentDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnRecommendationsResponseDto.HappnRecommendationDto.HappnRecommendationContentDto.HappnUserV1Dto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto.HappnProfileDto;
import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyMessageDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyProfileDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;

import static java.util.stream.Collectors.toList;

class HappnConverters {

    public static ProposalDto convert(HappnNotificationDto input) {
        return new ProposalDto(
                input.getNotifier().getId(),
                convert(input.getNotifier()));
    }

    public static ProposalDto convert(HappnRecommendationContentDto input) {
        return new ProposalDto(
                input.getUser().getId(),
                convert(input.getUser()));
    }

    public static ProfileDto convert(HappnUserDto input) {
        return new ProfileDto(
                input.getId(),
                input.getDisplay_name().orElse(""),
                input.getAge(),
                input.getAbout().orElse(null),
                input.getProfiles().stream().map(HappnProfileDto::getUrl).collect(toList()));
    }

    public static ProfileDto convert(HappnUserV1Dto input) {
        return new ProfileDto(
                input.getId(),
                input.getFirst_name(),
                input.getAge(),
                input.getAbout().orElse(null),
                input.getProfiles().stream().map(HappnProfileDto::getUrl).collect(toList()));
    }

    public static LazyProfileDto convertToLazy(HappnUserDto input) {
        return new LazyProfileDto(
                input.getId(),
                input.getDisplay_name().orElse(""),
                input.getProfiles().stream().map(HappnProfileDto::getUrl).findFirst().orElse(null));
    }

    public static ConversationDto convert(HappnConversationDto input) {
        return new ConversationDto(
                input.getId(),
                convertToLazy(input.getParticipants().get(1).getUser()),
                input.getModification_date().toZonedDateTime(),
                input.getMessages().isEmpty() ? null : convertToLazy(input.getMessages().get(0)));
    }

    public static MessageDto convert(HappnMessageDto input) {
        return new MessageDto(
                input.getId(),
                input.getCreation_date().toZonedDateTime(),
                input.getSender().flatMap(HappnUserDto::getBirth_date).isPresent(),
                input.getMessage());
    }

    public static LazyMessageDto convertToLazy(HappnMessageDto input) {
        return new LazyMessageDto(
                input.getCreation_date().toZonedDateTime(),
                input.getSender().flatMap(HappnUserDto::getBirth_date).isPresent(),
                input.getMessage());
    }
}
