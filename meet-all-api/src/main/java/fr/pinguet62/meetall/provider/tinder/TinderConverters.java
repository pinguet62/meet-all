package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.dto.ConversationDto;
import fr.pinguet62.meetall.dto.MessageDto;
import fr.pinguet62.meetall.dto.ProfileDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMatchDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderPhotoDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;

import java.time.temporal.ChronoUnit;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;

public class TinderConverters {

    public static ProfileDto convert(TinderUserDto input) {
        return new ProfileDto(
                input.get_id(),
                input.getName(),
                (int) ChronoUnit.YEARS.between(input.getBirth_date(), now()),
                input.getPhotos().stream().map(TinderPhotoDto::getUrl).collect(toList()));
    }

    public static ConversationDto convert(TinderMatchDto input, String currentUserId) {
        return new ConversationDto(
                input.get_id(),
                convert(input.getPerson()),
                input.getLast_activity_date(),
                input.getMessages().isEmpty() ? null : convert(input.getMessages().get(0), currentUserId));
    }

    public static MessageDto convert(TinderMessageDto input, String currentUserId) {
        return new MessageDto(
                input.get_id(),
                input.getSent_date(),
                input.getFrom().equals(currentUserId),
                input.getMessage());
    }

    public static MessageDto convert(TinderSendMessageResponseDto input) {
        return new MessageDto(
                input.get_id(),
                input.getSent_date(),
                true,
                input.getMessage());
    }

}
