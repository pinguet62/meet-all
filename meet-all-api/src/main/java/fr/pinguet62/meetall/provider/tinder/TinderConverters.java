package fr.pinguet62.meetall.provider.tinder;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyMessageDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyProfileDto;
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
                input.getBio().orElse(null),
                input.getPhotos().stream().map(TinderPhotoDto::getUrl).collect(toList()));
    }

    public static LazyProfileDto convertToLazy(TinderUserDto input) {
        return new LazyProfileDto(
                input.get_id(),
                input.getName(),
                input.getPhotos().stream().map(TinderPhotoDto::getUrl).findFirst().orElse(null));
    }

    public static ConversationDto convert(TinderMatchDto input, String currentUserId, Clock clock) {
        return new ConversationDto(
                input.get_id(),
                convertToLazy(input.getPerson()),
                input.getLast_activity_date(),
                input.getMessages().isEmpty() ? null : convertToLazy(input.getMessages().get(0), currentUserId));
    }

    public static MessageDto convert(TinderMessageDto input, String currentUserId) {
        return new MessageDto(
                input.get_id(),
                input.getSent_date(),
                input.getFrom().equals(currentUserId),
                input.getMessage());
    }

    public static LazyMessageDto convertToLazy(TinderMessageDto input, String currentUserId) {
        return new LazyMessageDto(
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
