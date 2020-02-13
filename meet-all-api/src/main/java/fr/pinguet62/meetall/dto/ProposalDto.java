package fr.pinguet62.meetall.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.util.Objects.requireNonNull;

@ToString
@EqualsAndHashCode
@Getter
public class ProposalDto {

    @NotNull
    private final String id;

    @NotNull
    @Valid
    private final ProfileDto profile; // TODO LazyProfileDto

    public ProposalDto(String id, ProfileDto profile) {
        this.id = requireNonNull(id);
        this.profile = requireNonNull(profile);
    }

    public ProposalDto withId(String value) {
        if (value.equals(this.id)) return this;
        return new ProposalDto(value, this.profile);
    }

    public ProposalDto withProfile(ProfileDto value) {
        if (value.equals(this.profile)) return this;
        return new ProposalDto(this.id, value);
    }

}
