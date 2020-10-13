package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.model.ProposalDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetConversationResponseDto.TinderGetConversationDataResponseDto.TinderMatchDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderGetRecommendationsResponseDto.TinderRecommendationDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderLikeResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderMessageDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderSendMessageResponseDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto;
import fr.pinguet62.meetall.provider.tinder.dto.TinderUserDto.TinderPhotoDto;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;

class TinderConverters {

    public static ProposalDto convert(TinderRecommendationDto input, Clock clock) {
        return new ProposalDto(
                input.getUser().get_id(),
                convert(input.getUser(), clock));
    }

    public static boolean convert(TinderLikeResponseDto input) {
        return !(input.getMatch() instanceof Boolean);
    }

    public static ProfileDto convert(TinderUserDto input, Clock clock) {
        return new ProfileDto(
                input.get_id(),
                input.getName(),
                (int) ChronoUnit.YEARS.between(input.getBirth_date(), now(clock)),
                input.getPhotos().stream().map(TinderPhotoDto::getUrl).collect(toList()));
    }

    public static ConversationDto convert(TinderMatchDto input, String currentUserId, Clock clock) {
        return new ConversationDto(
                input.get_id(),
                convert(input.getPerson(), clock),
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
