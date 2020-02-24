package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderRecommendationDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMatchDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderPhotoDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;

import java.time.temporal.ChronoUnit;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;

class TinderConverters {

    public static ProposalDto convert(TinderRecommendationDto input) {
        return new ProposalDto(
                input.getUser().getId(),
                convert(input.getUser()));
    }

    public static boolean convert(TinderLikeResponseDto input) {
        return !(input.getMatch() instanceof Boolean);
    }

    public static ProfileDto convert(TinderUserDto input) {
        return new ProfileDto(
                input.getId(),
                input.getName(),
                (int) ChronoUnit.YEARS.between(input.getBirth_date(), now()),
                input.getPhotos().stream().map(TinderPhotoDto::getUrl).collect(toList()));
    }

    public static ConversationDto convert(TinderMatchDto input, String currentUserId) {
        return new ConversationDto(
                input.getId(),
                convert(input.getPerson()),
                input.getLastActivityDate(),
                input.getMessages().isEmpty() ? null : convert(input.getMessages().get(0), currentUserId));
    }

    public static MessageDto convert(TinderMessageDto input, String currentUserId) {
        return new MessageDto(
                input.getId(),
                input.getSent_date(),
                input.getFrom().equals(currentUserId),
                input.getMessage());
    }

    public static MessageDto convert(TinderSendMessageResponseDto input) {
        return new MessageDto(
                input.getId(),
                input.getSentDate(),
                true,
                input.getMessage());
    }

}
