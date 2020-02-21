package fr.pinguet62.meetall.provider.once;

import fr.pinguet62.meetall.provider.model.ConversationDto;
import fr.pinguet62.meetall.provider.model.MessageDto;
import fr.pinguet62.meetall.provider.model.ProfileDto;
import fr.pinguet62.meetall.provider.once.dto.OnceConnectionDto;
import fr.pinguet62.meetall.provider.once.dto.OnceMessagesDto;
import fr.pinguet62.meetall.provider.once.dto.OncePictureDto;
import fr.pinguet62.meetall.provider.once.dto.OnceUserDto;

import java.time.Instant;
import java.time.ZoneId;

import static java.time.ZoneOffset.UTC;
import static java.util.stream.Collectors.toList;

class OnceConverters {

    private static final ZoneId ZONE_ID = UTC;

    public static ConversationDto convert(OnceConnectionDto input, String baseUrl) {
        return new ConversationDto(
                input.getMatch_id(),
                convert(input.getMatch_id(), input.getUser(), baseUrl),
                Instant.ofEpochSecond(input.getMessage_sent_at()).atZone(ZONE_ID),
                new MessageDto(
                        input.getMatch_id() + "::" + input.getLast_message_id(),
                        Instant.ofEpochSecond(input.getMessage_sent_at()).atZone(ZONE_ID),
                        !input.getSender_id().equals(input.getUser().getId()),
                        input.getLast_message()));
    }

    public static ProfileDto convert(String matchId, OnceUserDto input, String baseUrl) {
        return new ProfileDto(
                matchId,
                input.getFirst_name(),
                input.getAge().intValue(),
                input.getPictures().stream()
                        .map(OncePictureDto::getOriginal)
                        .map(it -> baseUrl + "/" + input.getId() + "/" + it)
                        .collect(toList()));
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
