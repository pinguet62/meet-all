package fr.pinguet62.meetall.provider.happn;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.dto.ProposalDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnConversationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnMessageDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnNotificationDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnProfileDto;
import fr.pinguet62.meetall.provider.happn.dto.HappnUserDto;

import static java.util.stream.Collectors.toList;

public class HappnConverters {

    public static ProposalDto convert(HappnNotificationDto input) {
        return new ProposalDto(
                input.getNotifier().getId(),
                convert(input.getNotifier()));
    }

    public static ProfileDto convert(HappnUserDto input) {
        return new ProfileDto(
                input.getId(),
                input.getDisplay_name(),
                input.getAge(),
                input.getProfiles().stream().map(HappnProfileDto::getUrl).collect(toList()));
    }

    public static ConversationDto convert(HappnConversationDto input) {
        return new ConversationDto(
                input.getId(),
                convert(input.getParticipants().get(1).getUser()),
                input.getModification_date().toZonedDateTime(),
                input.getMessages().isEmpty() ? null : convert(input.getMessages().get(0)));
    }

    public static MessageDto convert(HappnMessageDto input) {
        return new MessageDto(
                input.getId(),
                input.getCreation_date().toZonedDateTime(),
                input.getSender().getBirth_date() != null,
                input.getMessage());
    }

}
