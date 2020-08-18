package fr.pinguet62.meetall.provider.happn.dto;

import fr.pinguet62.meetall.provider.happn.GraphQLField;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Value
public class HappnUserDto {
    @NonNull
    String id;

    /**
     * <ol>
     * <li>{@code "CLIENT"} for normal user.</li>
     * <li>{@code "sponsor"} for pub.</li>
     * </ol>
     */
    @NonNull
    String type;

    @Nullable
    String display_name;

    @Nullable
    HappnRelation my_relation;

    @NonNull
    int age;

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
}
