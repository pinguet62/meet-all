package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyMessageDto;
import fr.pinguet62.meetall.provider.model.ConversationDto.LazyProfileDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConversationsResultDto.OnceConnectionDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesDto;
import fr.pinguet62.meetall.provider.once.dto.OnceUserDto;
import fr.pinguet62.meetall.provider.once.dto.OnceUserDto.OncePictureDto;

import java.time.Instant;
import java.time.ZoneId;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

class OnceConverters {

    private static final ZoneId ZONE_ID = UTC;

    public static ConversationDto convert(OnceConnectionDto input, String baseUrl) {
        return new ConversationDto(
                input.getMatch_id(),
                convertToLazy(input.getMatch_id(), input.getUser(), baseUrl),
                Instant.ofEpochSecond(input.getMessage_sent_at()).atZone(ZONE_ID),
                input.getLast_message_id().equals("0") ? null : new LazyMessageDto(
                        Instant.ofEpochSecond(input.getMessage_sent_at()).atZone(ZONE_ID),
                        !input.getSender_id().equals(input.getUser().getId()),
                        input.getLast_message()));
    }

    public static ProfileDto convert(String matchId, OnceUserDto input, String baseUrl) {
        return new ProfileDto(
                matchId,
                input.getFirst_name(),
                (int) input.getAge(),
                input.getDescription().orElse(null),
                input.getPictures().stream()
                        .map(OncePictureDto::getOriginal)
                        .map(it -> baseUrl + "/" + input.getId() + "/" + it)
                        .collect(toList()));
    }

    public static LazyProfileDto convertToLazy(String matchId, OnceUserDto input, String baseUrl) {
        return new LazyProfileDto(
                matchId,
                input.getFirst_name(),
                input.getPictures().stream()
                        .map(OncePictureDto::getOriginal)
                        .map(it -> baseUrl + "/" + input.getId() + "/" + it)
                        .findFirst()
                        .orElse(null));
    }

    public static MessageDto convert(OnceMessagesDto input, OnceUserDto user) {
        return new MessageDto(
                input.getId(),
                Instant.ofEpochSecond(input.getCreated_at()).atZone(ZONE_ID),
                !input.getSender_id().equals(user.getId()),
                input.getMessage());
    }

    public static MessageDto convertSentMessage(OnceMessagesDto input) {
        return new MessageDto(
                input.getId(),
                Instant.ofEpochSecond(input.getCreated_at()).atZone(ZONE_ID),
                true,
                input.getMessage());
    }

}
