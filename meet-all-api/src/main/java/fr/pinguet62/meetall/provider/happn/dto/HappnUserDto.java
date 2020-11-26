package fr.pinguet62.meetall.provider.happn.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

@Value
public class HappnUserDto {
    @RequiredArgsConstructor
    public enum HappnRelation {
        NEW_RELATION(0),
        ACCEPTED_BY_ME(1), // but not (yet) by target
        MATCHED(2),
        DISCUSSING(4);

        private final int value;

        @JsonCreator
        public static HappnRelation fromValue(int input) {
            return stream(values())
                    .filter(it -> it.value == input)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown relation value " + input));
        }

        @JsonValue
        public int toValue() {
            return value;
        }
    }

    @Value
    public static class HappnProfileDto {
        @NonNull
        String url;
    }

    @NonNull
    String id;

    /**
     * <ol>
     * <li>{@code "CLIENT"} or {@code "client"} for normal user.</li>
     * <li>{@code "sponsor"} for pub.</li>
     * </ol>
     */
    @NonNull
    String type;

    @Nullable
    String display_name;

    @Nullable
    HappnRelation my_relation;

    // TODO refactor model: can be null
    // @JsonProperty(required = true)
    Integer renewable_likes;

    // TODO refactor model: can be null
    // @JsonProperty(required = true)
    Integer age;

    String about;

    @NonNull
    @GraphQLField(additional = ".mode(0).width(1000).height(1000)")
    List<HappnProfileDto> profiles;

    /**
     * @example {@code 1989-06-14}
     */
    @Nullable
    LocalDate birth_date;

    public Optional<String> getDisplay_name() {
        return ofNullable(display_name);
    }

    public Optional<HappnRelation> getMy_relation() {
        return ofNullable(my_relation);
    }

    public Optional<LocalDate> getBirth_date() {
        return ofNullable(birth_date);
    }

    public Optional<String> getAbout() {
        return about == null || about.isEmpty() ? empty() : of(about);
    }
}
